package com.echo.pokepedia.ui.pokemon.settings

import android.net.Uri

interface EditPhotoBottomSheetListener {

    fun onTakenOrSelectedPhoto(imgUri: Uri?)

    fun onRemovePhoto()
}