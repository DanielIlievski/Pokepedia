package com.echo.pokepedia.ui

import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun showToastMessage(message: String?, duration: Int) {
        Toast.makeText(requireContext(), message, duration).show()
    }
}