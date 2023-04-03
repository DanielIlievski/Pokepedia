package com.echo.pokepedia.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentLoginBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    // endregion

    // region fragment methods
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
    }

    private fun initListeners() {
        onSignUpClickListener()
        onLoginClickListener()
        removeErrorMessageListener()
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

    private fun observeLoginUser() = lifecycleScope.launch {
        viewModel.loginUser.collect {result ->
            when (result) {
                is Resource.Success -> onSuccessfulLogin()
                is Resource.Failure -> onFailedLogin(result.exception)
            }
        }
    }

    private fun onSuccessfulLogin() {
        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
    }

    private fun onFailedLogin(e: Exception) {
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
    }
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
    // endregion
}