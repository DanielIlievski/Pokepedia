package com.echo.pokepedia.ui.pokemon.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentSettingsBinding
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.toDDMMMYYYY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

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

        observeCurrentUser()

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun observeCurrentUser() = lifecycleScope.launch {
        viewModel.currentUser.collectLatest { user ->
            initUi(user)
        }
    }

    private fun initUi(user: User) {
        with(binding) {
            loadImage(user.profilePicture, imgProfile)
            textNameSurname.text = user.fullName
            textEmail.text = user.email
            textStartDate.text =
                getString(R.string.start_date, user.date?.toDDMMMYYYY())
        }
    }

    private fun initListeners() {
        onImageEditClickListener()
        onLogoutClickListener()
        onChangePasswordClickListener()
    }

    // region initListeners
    private fun onImageEditClickListener() {}

    private fun onLogoutClickListener() {}

    private fun onChangePasswordClickListener() {}
    // endregion
}