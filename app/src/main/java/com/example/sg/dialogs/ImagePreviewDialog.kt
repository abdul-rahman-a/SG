package com.example.sg.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.sg.R

class ImagePreviewDialog(private val imageUri: Uri) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_image_preview, null)

        val imageView = view.findViewById<ImageView>(R.id.previewImageView)
        imageView.setImageURI(imageUri)

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle("Image Preview")
            .setPositiveButton("Close") { _, _ -> dismiss() }
            .create()
    }
}