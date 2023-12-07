package com.example.sg.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.sg.R
import com.example.sg.dialogs.ImagePreviewDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class ImageUploadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var activity: FragmentActivity

    private var btnSelectImage: Button
    private var btnPreview: Button
    private var btnSubmit: Button
    private var tvFileName: TextView
    private var tvFileType: TextView

    private var selectedImageUri: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var capturedImageURI: Uri? = null
    private val cameraPermission = Manifest.permission.CAMERA
    private val galleryPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    private var imageUploadListener: ImageUploadListener? = null

    lateinit var permissionCamera: ActivityResultLauncher<String>
    lateinit var permissionGallery: ActivityResultLauncher<String>
    lateinit var cameraRequest: ActivityResultLauncher<Intent>
    lateinit var galleryRequest: ActivityResultLauncher<Intent>

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.image_upload_component, this, true)

        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnPreview = findViewById(R.id.btnPreview)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvFileName = findViewById(R.id.tvFileName)
        tvFileType = findViewById(R.id.tvFileType)

        btnSelectImage.setOnClickListener { showImageSourceOptions() }
        btnPreview.setOnClickListener { previewImage() }
        btnSubmit.setOnClickListener { submitImage() }
    }

    fun setImageUploadListener(listener: ImageUploadListener) {
        this.imageUploadListener = listener
    }

    // Function to update file information (name and type) on the UI
    private fun updateFileInfo() {
        val (fileName, fileType) = getFileNameAndType(selectedImageUri)
        tvFileName.text = "Selected File: $fileName"
        tvFileType.text = "Selected File Type: $fileType"
    }

    // Inside the ImageUploadView class
    private fun previewImage() {
        selectedImageUri?.let {
            val fragmentManager = getFragmentManagerForDialog()
            ImagePreviewDialog(it).show(fragmentManager, "ImagePreviewDialog")
        }
    }

    private fun getFragmentManagerForDialog(): FragmentManager {
        return activity.supportFragmentManager ?: throw IllegalStateException("Activity must be a FragmentActivity")
    }

    // Function to simulate image submission (placeholder for actual implementation)
    private fun submitImage() {
        selectedImageUri?.let {
            imageUploadListener?.onImageSubmitted(selectedImageUri)
        }
    }

    // Function to extract file name and type from a Uri
    private fun getFileNameAndType(uri: Uri?): Pair<String, String> {
        val cursor = uri?.let {
            context.contentResolver.query(it, null, null, null, null)
        }

        val fileNameWithExtension = cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        } ?: "Unknown"

        val fileName = fileNameWithExtension.substringBeforeLast('.', "")
        val fileType = fileNameWithExtension.substringAfterLast('.', "")

        return Pair(fileName, fileType)
    }

    // Function to display a dialog for selecting the image source (camera or gallery)
    private fun showImageSourceOptions() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(context)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    // Function to handle opening the camera based on permissions
    private fun openCamera() {
        if (checkPermission()) takePicture() else requestCameraPermission()
    }

    // Function to handle opening the gallery based on permissions
    private fun openGallery() {
        if (checkPermission()) openGalleryForImages() else requestGalleryPermission()
    }

    // Function to check camera and gallery permissions
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    galleryPermission
                ) == PackageManager.PERMISSION_GRANTED)
    }

    // Function to request camera permission using the ActivityResult API
    private fun requestCameraPermission() {
        permissionCamera.launch(cameraPermission)
    }

    fun initialiseRegisterForResult(activity: FragmentActivity) {
        this.activity = activity
        // Camera permission result callback
        permissionCamera =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
                if (permission) {
                    takePicture()
                } else {
                    showMessage("Camera Permission Denied")
                }
            }

        // Gallery permission result callback
        permissionGallery =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
                if (permission) {
                    openGalleryForImages()
                } else {
                    showMessage("Gallery Permission Denied")
                }
            }

        // Gallery activity result callback
        galleryRequest =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.data?.data != null) {
                    selectedImageUri = result.data!!.data
                    updateFileInfo()
                }
            }

        // Camera activity result callback
        cameraRequest =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    selectedImageUri = capturedImageURI
                    updateFileInfo()
                } else {
                    showMessage("Image Capture Cancelled")
                }
            }
    }



    // Function to request gallery permission using the ActivityResult API
    private fun requestGalleryPermission() {
        permissionGallery.launch(galleryPermission)
    }



    // Function to open the gallery for selecting images
    @SuppressLint("QueryPermissionsNeeded")
    private fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooserIntent = Intent.createChooser(intent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        if (chooserIntent.resolveActivity(context.packageManager) != null) {
            galleryRequest.launch(chooserIntent)
        }
    }

    // Function to capture a picture using the camera
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createImageFile()

        capturedImageURI = FileProvider.getUriForFile(
            context,
            "com.example.sg.fileProvider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI)
        cameraRequest.launch(intent)
    }



    // Function to create a temporary image file
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    interface ImageUploadListener {
        fun onImageSubmitted(imageUri: Uri?)
    }
}

