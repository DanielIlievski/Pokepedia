package com.echo.pokepedia.ui

import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun showToastMessageShort(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showToastMessageLong(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun showToastMessageCustom(message: String?, duration: Int) {
        Toast.makeText(requireContext(), message, duration).show()
    }
}