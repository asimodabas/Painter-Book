package com.asimodabas.painter_book

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asimodabas.painter_book.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.lang.Exception


class DetailsActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityDetailsBinding
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        binding.button.setOnClickListener {
            saveButtonClicked(it)
        }

        binding.imageView.setOnClickListener {
            selectImage(it)
        }


    }

    fun saveButtonClicked(view: View) {

        val artName = binding.artNameText.text.toString()
        val artistName = binding.artistNameText2.text.toString()
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 310)

            val outPutStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outPutStream)
            val byteArray = outPutStream.toByteArray()

            try {
                val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)")

                val sqlString =
                    "INSERT INTO arts(artname, artistname, year, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindString(2, artistName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)
                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(this@DetailsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {

        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {

            //landScape Image
            width = maximumSize

            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()


        } else {

            //portraid Image
            height = maximumSize

            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()

        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                //Snackbar
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission", View.OnClickListener {

                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }).show()

            } else {

                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        } else {

            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }

    }

    private fun registerLauncher() {

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        // binding.imageView.setImageURI(imageData)
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        this@DetailsActivity.contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        contentResolver,
                                        imageData
                                    )
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                } else {
                    Toast.makeText(this@DetailsActivity, "Permission needed", Toast.LENGTH_LONG)
                        .show()
                }
            }

    }

}