package com.echo.pokepedia.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.echo.pokepedia.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<VModel: BaseViewModel> : Fragment() {

    protected lateinit var viewModel: VModel
    protected abstract fun getViewModelClass(): Class<VModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[getViewModelClass()]
    }

    fun showToastMessageShort(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showToastMessageLong(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun showToastMessageCustom(message: String?, duration: Int) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    fun getGradientWhiteBottom(color: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                color,
                Color.WHITE
            )
        )
    }

    fun getGradientBLTRBottomRounded(color1: Int, color2: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                color1,
                color2
            )
        ).apply { cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 60f, 60f, 60f, 60f) }
    }

    fun getGradientBlackTop(color: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                color,
                Color.BLACK
            )
        )
    }

    fun loadImage(imgUrl: String?, imageView: ImageView, placeholder: Int = R.drawable.progress_spinner_anim) {
        Glide.with(requireContext())
            .load(imgUrl)
            .placeholder(placeholder)
            .into(imageView)
    }

    fun showSimpleAlertDialog(
        context: Context,
        title: Int? = null,
        message: Int? = null,
        positiveBtnText: Int? = null,
        negativeBtnText: Int? = null,
        onPositiveBtnClick: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
        onNegativeBtnClick: ((dialog: DialogInterface, which: Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setTitle(title?.let { getString(it) })
            .setMessage(message?.let { getString(it) })
            .setCancelable(false)
            .setPositiveButton(
                positiveBtnText?.let { getString(it) },
                onPositiveBtnClick?.let { DialogInterface.OnClickListener(it) }
            )
            .setNegativeButton(
                negativeBtnText?.let { getString(it) },
                onNegativeBtnClick?.let { DialogInterface.OnClickListener(it) }
            )
            .create()
            .show()
    }

    fun observeErrorObservable() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.errorObservable.collectLatest { exceptionMessage ->
                val msg = exceptionMessage.asString(requireContext())
                if (msg.isNotEmpty()) {
                    showToastMessageLong(msg)
                }
            }
        }
    }
}