package com.echo.pokepedia.ui.pokemon.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.DialogEditProfilePhotoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditPhotoBottomSheetFragment(
    private val editPhotoBottomSheetListener: EditPhotoBottomSheetListener
) : BottomSheetDialogFragment() {

    // region fragment variables
    private var _binding: DialogEditProfilePhotoBinding? = null
    private val binding get() = _binding!!

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var imgUri: Uri? = null

    private val requestPermissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                takeImage()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                editPhotoBottomSheetListener.onTakenOrSelectedPhoto(imgUri)
            }
        }

    private val selectImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imgUri = result.data?.data
                editPhotoBottomSheetListener.onTakenOrSelectedPhoto(imgUri)
                dismiss()
            }
        }
    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditProfilePhotoBinding.inflate(inflater, container, false)
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
    // endregion

    private fun initListeners() {
        onCameraClickListener()
        onGalleryClickListener()
        onRemovePhotoClickListener()
    }

    // region initListeners

    // region onCameraClickListener
    private fun onCameraClickListener() {
        binding.txtBtnCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                takeImage()
                return@setOnClickListener
            } else {
                if (hasPermissions()) {
                    takeImage()
                } else {
                    requestPermissionsResult.launch(PERMISSIONS)
                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(requireContext(), it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun takeImage() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imgUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        val cameraIntent = Intent().apply {
            action = MediaStore.ACTION_IMAGE_CAPTURE
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        takeImageResult.launch(cameraIntent)
    }
    // endregion

    // region onGalleryClickListener
    private fun onGalleryClickListener() {
        binding.txtBtnGallery.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val galleryIntent = Intent().apply {
            action = Intent.ACTION_PICK
            type = "image/*"
        }
        selectImageResult.launch(Intent.createChooser(galleryIntent, "Choose from"))
    }
    // endregion

    // region onRemovePhotoClickListener
    private fun onRemovePhotoClickListener() {
        binding.btnRemovePhoto.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_photo_title,
                R.string.remove_photo_message,
                R.string.yes,
                R.string.no,
                { _, _ ->
                    editPhotoBottomSheetListener.onRemovePhoto()
                    dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun showSimpleAlertDialog(
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
    // endregion
    // endregion
}