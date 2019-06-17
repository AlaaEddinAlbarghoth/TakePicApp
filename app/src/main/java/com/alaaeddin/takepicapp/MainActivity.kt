package com.alaaeddin.takepicapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception
import java.util.*

@Suppress("PrivatePropertyName")
class MainActivity : AppCompatActivity() {

    private val RequestCode = 1
    private var fileName: String = ""
    private var pathToFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 2
        )

        setupListeners()
    }

    private fun setupListeners() {
        btn_takePic.setOnClickListener {
            takePic()
        }
    }

    private fun takePic() {
        /** Here we need to define the action to open the camera */
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            createPhotoFile()?.let { file ->
                pathToFile = file.absolutePath
                val uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.alaaeddin.takepicapp.fileprovider",
                    file
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, RequestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode) {
            iv_image.setImageBitmap(BitmapFactory.decodeFile(pathToFile))
        }
    }

    private fun createPhotoFile(): File? {
        fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        fileName += "IMG_$fileName"
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var image: File? = null
        try {
            image = File.createTempFile(
                fileName, /* prefix */
                ".jpg", /* suffix */
                dir /* directory */
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return image
    }
}
