package com.echo.pokepedia.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.echo.pokepedia.databinding.FragmentLoginBinding
import com.echo.pokepedia.ui.BaseFragment

class LoginFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion
}