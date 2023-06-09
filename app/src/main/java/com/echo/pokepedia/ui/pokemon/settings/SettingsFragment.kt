package com.echo.pokepedia.ui.pokemon.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentSettingsBinding
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.toDDMMMYYYY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), EditPhotoBottomSheetListener {

    // region fragment variables
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

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
        observeCurrentUser()
        observeSettingsViewState()
    }

    private fun initListeners() {
        onImageEditClickListener()
        onLogoutClickListener()
    }

    // region initObservers

    // region observeCurrentUser
    private fun observeCurrentUser() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.currentUser.collect { user ->
                if (user != null) {
                    initUI(user)
                }
            }
        }
    }

    private fun initUI(user: User) {
        with(binding) {
            loadImage(user.profilePicture, imgProfile, R.drawable.profile_placeholder)
            textNameSurname.text = user.fullName
            textEmail.text = user.email
            textStartDate.text =
                getString(R.string.start_date, user.date?.toDDMMMYYYY())
        }
    }
    // endregion

    private fun observeSettingsViewState() = lifecycleScope.launch {
        viewModel.settingsViewState.collect { viewState ->
            when (viewState) {
                SettingsViewState.LogoutSuccessful -> {
                    showToastMessageShort(getString(R.string.logout_successful))
                    val action = SettingsFragmentDirections.settingsFragmentToAuthActivity()
                    findNavController().navigate(action)
                    activity?.finish()
                }
                SettingsViewState.EmptyViewState -> {}
            }
        }
    }
    // endregion

    // region initListeners

    // region onImageEditClickListener
    private fun onImageEditClickListener() {
        binding.imgProfileEdit.setOnClickListener {
            val bottomSheet = EditPhotoBottomSheetFragment(this)
            bottomSheet.show(childFragmentManager, "Edit photo")
        }
    }
    // endregion

    private fun onLogoutClickListener() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
    // endregion

    override fun onTakenOrSelectedPhoto(imgUri: Uri?) {
        viewModel.updateProfilePhoto(imgUri)
    }

    override fun onRemovePhoto() {
        viewModel.updateProfilePhoto(null)
    }
}