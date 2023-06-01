package com.echo.pokepedia.ui.authentication.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echo.pokepedia.BottomSheetListener
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentLoginBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import com.echo.pokepedia.util.facebookPermissionsList
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment(), BottomSheetListener {

    // region fragment variables
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var callbackManager: CallbackManager
    // endregion

    // region fragment methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        callbackManager = CallbackManager.Factory.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isUserAuthenticated()) {
            navigateToHomeScreen()
        }

        initObservers()

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun initObservers() {
        observeViewState()
        observeSignInUser()
        observeResetPassword()
    }

    private fun initListeners() {
        onSignUpClickListener()
        onLoginClickListener()
        removeErrorMessageListener()
        onFacebookSignInClickListener()
        onGoogleSignInClickListener()
        onForgotPasswordClickListener()
    }

    // region initObservers

    // region observeViewState
    private fun observeViewState() = lifecycleScope.launch {
        viewModel.viewState.collect { viewState ->
            when (viewState) {
                LoginViewState.EmptyEmailField -> onEmptyEmailField()
                LoginViewState.EmptyPasswordField -> onEmptyPasswordField()
                LoginViewState.EmptyViewState -> {}
            }
        }
    }

    private fun onEmptyEmailField() {
        binding.textEmail.apply {
            error = getString(R.string.enter_email)
            errorIconDrawable = null
        }
    }

    private fun onEmptyPasswordField() {
        binding.textPassword.apply {
            error = getString(R.string.enter_password)
            errorIconDrawable = null
        }
    }
    // endregion

    // region observeSignInUser
    private fun observeSignInUser() = lifecycleScope.launch {
        viewModel.signInUser.collect { result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulLogin(result.result)
                is NetworkResult.Failure -> onFailedLogin(result.exception)
            }
        }
    }

    private fun onSuccessfulLogin(user: FirebaseUser?) {
        showToastMessageShort(getString(R.string.sign_in_successful, user?.displayName))
        navigateToHomeScreen()
    }

    private fun onFailedLogin(e: UiText?) {
        showToastMessageLong(e?.asString(requireContext()))
    }
    // endregion

    // region observeResetPassword
    private fun observeResetPassword() = lifecycleScope.launch {
        viewModel.resetPassword.collect { result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulPasswordReset()
                is NetworkResult.Failure -> onFailedPasswordReset(result.exception)
            }
        }
    }

    private fun onSuccessfulPasswordReset() {
        showToastMessageShort(getString(R.string.password_reset_successful))
    }

    private fun onFailedPasswordReset(e: UiText?) {
        showToastMessageLong(e?.asString(requireContext()))
    }
    // endregion
    // endregion

    // region initListeners
    private fun onSignUpClickListener() {
        binding.buttonGoToSignUp.setOnClickListener {
            val action = LoginFragmentDirections.loginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
    }

    private fun onLoginClickListener() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.textEmail.editText?.text.toString()
            val password = binding.textPassword.editText?.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun removeErrorMessageListener() {
        binding.apply {
            textEmail.editText?.addTextChangedListener { textEmail.error = null }
            textPassword.editText?.addTextChangedListener { textPassword.error = null }
        }
    }

    // region onFacebookSignInClickListener
    private fun onFacebookSignInClickListener() {
        binding.buttonFacebook.setOnClickListener {
            facebookSignIn()
        }
    }

    private fun facebookSignIn() {
        LoginManager.getInstance().logOut()
        LoginManager.getInstance().apply {
            logInWithReadPermissions(
                this@LoginFragment,
                callbackManager,
                facebookPermissionsList
            )
            registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {}

                override fun onError(error: FacebookException) {}

                override fun onSuccess(result: LoginResult) {
                    viewModel.facebookSignIn(result.accessToken)
                }
            })
        }
    }
    // endregion

    // region onGoogleSignInClickListener
    private fun onGoogleSignInClickListener() {
        binding.buttonGoogle.setOnClickListener {
            googleSignIn()
        }
    }

    private fun googleSignIn() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.googleSignIn(task)
            }
        }
    // endregion

    private fun onForgotPasswordClickListener() {
        binding.buttonForgotPassword.setOnClickListener {
            val bottomSheet = ResetPasswordBottomSheet(this)
            bottomSheet.show(childFragmentManager, "Password reset")
        }
    }

    override fun onSendPasswordResetClickListener(email: String) {
        viewModel.resetPassword(email)
    }

    private fun navigateToHomeScreen() {
        val action = LoginFragmentDirections.loginFragmentToHomeScreenActivity()
        findNavController().navigate(action)

        activity?.finish()
    }
    // endregion
}