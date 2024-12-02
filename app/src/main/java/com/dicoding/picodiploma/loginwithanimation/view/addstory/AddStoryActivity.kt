package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var ivPreview: ImageView
    private lateinit var etDescription: EditText
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnUpload: Button
    private var imageUri: Uri? = null

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        ivPreview = findViewById(R.id.ivPreview)
        etDescription = findViewById(R.id.etDescription)
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)
        btnUpload = findViewById(R.id.btnUpload)

        btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 100)
        }

        btnGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 200)
        }

        btnUpload.setOnClickListener {
            val description = etDescription.text.toString()
            if (imageUri != null && description.isNotEmpty()) {
                val file = uriToFile(imageUri!!, this)
                viewModel.uploadStory(description, file).observe(this) { result ->
                    result.onSuccess {
                        Toast.makeText(this, "Story uploaded successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure {
                        Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please add an image and description.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    ivPreview.setImageBitmap(bitmap)
                    imageUri = getImageUri(this, bitmap)
                }
                200 -> {
                    imageUri = data?.data
                    ivPreview.setImageURI(imageUri)
                }
            }
        }
    }

    private fun getImageUri(activity: Activity, bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "Story Image", null)
        return Uri.parse(path)
    }

    private fun uriToFile(selectedUri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("temp", null, context.cacheDir)
        val inputStream = contentResolver.openInputStream(selectedUri) ?: return myFile
        val outputStream = FileOutputStream(myFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return myFile
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
