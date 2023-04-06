package com.echo.pokepedia.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.echo.pokepedia.BottomSheetListener
import com.echo.pokepedia.databinding.BottomSheetPasswordResetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResetPasswordBottomSheet(
    private val bottomSheetListener: BottomSheetListener
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPasswordResetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPasswordResetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        onCancelClickListener()
        onSendClickListener()
    }

    private fun onCancelClickListener() {
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun onSendClickListener() {
        binding.buttonSend.setOnClickListener {
            bottomSheetListener.onButtonClickListener(binding.textEmail.editText?.text.toString())
            dismiss()
        }
    }

}