package com.echo.pokepedia.ui.authentication.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentRegisterBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.ui.authentication.login.RegisterViewModel
import com.echo.pokepedia.ui.authentication.login.RegisterViewState
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<RegisterViewModel>() {

    // region fragment variables
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

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

    override fun getViewModelClass(): Class<RegisterViewModel> = RegisterViewModel::class.java
    // endregion

    private fun initObservers() {
        observeViewState()
        observeRegisterUser()
    }

    private fun initListeners() {
        onGoToLoginClickListener()
        onRegisterClickListener()
        removeErrorMessageListener()
    }

    // region initObservers

    // region observeViewState
    private fun observeViewState() = lifecycleScope.launch {
        viewModel.viewState.collect { viewState ->
            when (viewState) {
                RegisterViewState.EmptyEmailField -> onEmptyEmailField()
                RegisterViewState.EmptyPasswordField -> onEmptyPasswordField()
                RegisterViewState.InvalidEmail -> onInvalidEmail()
                RegisterViewState.PasswordsDoNotMatch -> onPasswordsDoNotMatch()
                RegisterViewState.WeakPassword -> onWeakPassword()
                RegisterViewState.EmptyFirstNameField -> onEmptyFirstNameField()
                RegisterViewState.EmptyLastNameField -> onEmptyLastNameField()
                RegisterViewState.EmptyViewState -> {}
                else -> {}
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

    private fun onInvalidEmail() {
        binding.textPassword.apply {
            error = getString(R.string.invalid_email)
            errorIconDrawable = null
        }
    }

    private fun onPasswordsDoNotMatch() {
        binding.textPassword.apply {
            error = getString(R.string.passwords_mismatch)
            errorIconDrawable = null
        }
        binding.textRepeatPassword.apply {
            error = getString(R.string.passwords_mismatch)
            errorIconDrawable = null
        }
    }

    private fun onWeakPassword() {
        binding.textPassword.apply {
            error = getString(R.string.weak_password)
            errorIconDrawable = null
        }
    }

    private fun onEmptyFirstNameField() {
        binding.textFirstName.apply {
            error = getString(R.string.enter_first_name)
            errorIconDrawable = null
        }
    }

    private fun onEmptyLastNameField() {
        binding.textLastName.apply {
            error = getString(R.string.enter_last_name)
            errorIconDrawable = null
        }
    }
    // endregion

    // region observeRegisterUser
    private fun observeRegisterUser() = lifecycleScope.launch {
        viewModel.registerUser.collect { result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulRegistration()
                is NetworkResult.Failure -> onFailedRegistration(result.exception)
            }
        }
    }

    private fun onSuccessfulRegistration() {
        showToastMessageShort(getString(R.string.successful_registration))

        val action = RegisterFragmentDirections.registerFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun onFailedRegistration(e: UiText?) {
        showToastMessageLong(e?.asString(requireContext()))
    }
    // endregion
    // endregion

    // region initListeners
    private fun onGoToLoginClickListener() {
        binding.buttonGoToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.registerFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

    private fun onRegisterClickListener() {
        binding.buttonRegister.setOnClickListener {
            val firstName = binding.textFirstName.editText?.text.toString()
            val lastName = binding.textLastName.editText?.text.toString()
            val email = binding.textEmail.editText?.text.toString()
            val password = binding.textPassword.editText?.text.toString()
            val repeatPassword = binding.textRepeatPassword.editText?.text.toString()
            viewModel.register(firstName, lastName, email, password, repeatPassword)
        }
    }

    private fun removeErrorMessageListener() {
        binding.apply {
            textFirstName.editText?.addTextChangedListener { textFirstName.error = null }
            textLastName.editText?.addTextChangedListener { textLastName.error = null }
            textEmail.editText?.addTextChangedListener { textEmail.error = null }
            textPassword.editText?.addTextChangedListener { textPassword.error = null }
            textRepeatPassword.editText?.addTextChangedListener { textRepeatPassword.error = null }
        }
    }
    // endregion

}