package com.example.multikskills.uploadimagefirebasestorage

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    var PICK_IMAGE_REQUEST = 111
    private var fileUri: Uri? = null
    private var bitmap: Bitmap? = null
    var pd: ProgressDialog? = null

    //creating reference to firebase storage
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReferenceFromUrl("gs://frscmobile.appspot.com")    //change the url according to your firebase app


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
      val  database = FirebaseDatabase.getInstance().getReference("frsc")
        val actionBar = supportActionBar
        if (null != actionBar) {
            actionBar.hide()
        }

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        val strDate = dateFormat.format(date).toString()

        pd = ProgressDialog(this)
        pd?.setMessage("Uploading....")
        chooseImg.setOnClickListener {

                val intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_PICK)
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
        uploadImg.setOnClickListener {

                if (fileUri != null)
                {
                    pd?.show()
                    val childRef = storageRef.child("image.jpg")
                    //uploading the image
                    val uploadTask = childRef.putFile(fileUri!!)
                    uploadTask.addOnSuccessListener(object: OnSuccessListener<UploadTask.TaskSnapshot> {
                     override   fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                            pd?.dismiss()
                         val upload=Uploadclass(editText2.text.toString().trim(),editText3.text.toString().trim(),editText4.text.toString().trim(),editText5.text.toString().trim(),strDate,taskSnapshot.downloadUrl.toString())
                            Toast.makeText(this@MainActivity, "Upload successful"+taskSnapshot.downloadUrl, Toast.LENGTH_SHORT).show()
                         database.child(editText2.text.toString()).setValue(upload)

                     }
                    }).addOnFailureListener(object: OnFailureListener {
                       override fun onFailure(@NonNull e:Exception) {
                            pd?.dismiss()
                            Toast.makeText(this@MainActivity, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else
                {
                    Toast.makeText(this@MainActivity, "Select an image", Toast.LENGTH_SHORT).show()
                }
            }



    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (bitmap != null) {
            bitmap!!.recycle()
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                imgView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
