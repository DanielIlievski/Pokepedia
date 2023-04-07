package com.echo.pokepedia.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentLoginBinding
import com.echo.pokepedia.ui.BaseFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.echo.pokepedia.util.NetworkResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    // endregion

    // region fragment methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
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
        observeLoginUser()
        observeGoogleSignInUser()
    }

    private fun initListeners() {
        onSignUpClickListener()
        onLoginClickListener()
        removeErrorMessageListener()
        onGoogleSignInClickListener()
    }

    // region initObservers
    private fun observeViewState() = lifecycleScope.launch {
        viewModel.viewState.collect { viewState ->
            when (viewState) {
                LoginViewState.EmptyEmailField -> onEmptyEmailField()
                LoginViewState.EmptyPasswordField -> onEmptyPasswordField()
                LoginViewState.EmptyViewState -> {}
            }
        }
    }

    // region viewState methods
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

    // region observeLoginUser
    private fun observeLoginUser() = lifecycleScope.launch {
        viewModel.loginUser.collect { result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulLogin(result.result)
                is NetworkResult.Failure -> onFailedLogin(result.exception)
            }
        }
    }

    private fun onSuccessfulLogin(user: FirebaseUser?) {
        showToastMessage("${user?.displayName}, login was successful!", Toast.LENGTH_SHORT)
    }

    private fun onFailedLogin(e: Exception) {
        showToastMessage(e.message, Toast.LENGTH_LONG)
    }
    // endregion

    // region observeGoogleSignInUser
    private fun observeGoogleSignInUser() = lifecycleScope.launch {
        viewModel.googleSignInUser.collect { result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulGoogleSignIn(result.result)
                is NetworkResult.Failure -> onFailedGoogleSignIn(result.exception)
            }
        }
    }

    private fun onSuccessfulGoogleSignIn(user: FirebaseUser?) {
        Toast.makeText(requireContext(), "${user!!.displayName}, google sign in was successful", Toast.LENGTH_SHORT).show()
    }

    private fun onFailedGoogleSignIn(e: Exception) {
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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
    // endregion
}