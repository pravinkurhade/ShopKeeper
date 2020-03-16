package com.bsktech.shopkeeper

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bsktech.shopkeeper.mlkit.LiveBarcodeScanningActivity
import com.bsktech.shopkeeper.models.StoreItem
import com.bsktech.shopkeeper.utils.AppUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.content_add_product.*
import java.io.ByteArrayOutputStream


class AddProductActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    var image: Bitmap? = null
    var update: Boolean = false

    private var productData = StoreItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        if (intent.extras != null) {
            productData = intent.getParcelableExtra<StoreItem>("storeItem")
            update = true
            setData()
        }

        imageView_product.setOnClickListener {
            selectImage(this)
        }

        button_save.setOnClickListener {
            if (validateData()) {
                saveProduct()
            }
        }

        imageView_qr.setOnClickListener {
            startActivityForResult(Intent(this, LiveBarcodeScanningActivity::class.java), 2)
        }
    }

    private fun setData() {
        val storageRef = storage.reference
        val pathReference = storageRef.child(productData.image!![0])
        Glide.with(this).load(pathReference).into(imageView_product)

        editText_name.setText(productData.name)
        editText_size.setText(productData.size)
        editText_price.setText(productData.price.toString())
        editText_code.setText(productData.productCode)

        button_save.text = "Update"
        supportActionBar?.title = "Update Product"
    }

    private fun saveProduct() {
        if (update) {
            if (image != null) {
                val imageName =
                    "StoreItems/store_" + auth.currentUser?.uid + "_" + System.currentTimeMillis() + ".jpg"
                val baos = ByteArrayOutputStream()
                image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val storageRef = storage.reference
                val mountainsRef =
                    storageRef.child(imageName)
                var uploadTask = mountainsRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    var arrayList = ArrayList<String>()
                    arrayList.add(imageName)
                    productData.image = arrayList
                    db.collection("StoreItems").document(productData._id!!).set(productData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product Saved Successfully.", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                }
                // Observe state change events such as progress, pause, and resume
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    println("Upload is $progress% done")
                }.addOnPausedListener {
                    println("Upload is paused")
                }
            } else {
                db.collection("StoreItems").document(productData._id!!).set(productData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product Saved Successfully.", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
            }
        } else {
            val imageName =
                "StoreItems/store_" + auth.currentUser?.uid + "_" + System.currentTimeMillis() + ".jpg"
            val baos = ByteArrayOutputStream()
            image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val storageRef = storage.reference
            val mountainsRef =
                storageRef.child(imageName)
            var uploadTask = mountainsRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                var arrayList = ArrayList<String>()
                arrayList.add(imageName)
                productData.image = arrayList
                db.collection("StoreItems").add(productData).addOnSuccessListener {
                    Toast.makeText(this, "Product Saved Successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            // Observe state change events such as progress, pause, and resume
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                println("Upload is $progress% done")
            }.addOnPausedListener {
                println("Upload is paused")
            }
        }
    }

    private fun validateData(): Boolean {

        if (!update) {
            if (image == null) {
                Toast.makeText(this, "Please Select Image for Product.", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (editText_name.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Product Name.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (editText_size.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Product Size.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (editText_code.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Product Code.", Toast.LENGTH_SHORT).show()
            return false
        }

        productData.name = editText_name.text.toString().toLowerCase()
        productData.size = editText_size.text.toString()
        productData.price = editText_price.text.toString().toDouble()
        productData.productCode = editText_code.text.toString()
        productData.storeId = auth.currentUser?.uid
        productData.productCodeStoreId = editText_code.text.toString() + "_" + auth.currentUser?.uid
        productData.timestamp = System.currentTimeMillis()
        return true
    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Product Image")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (Activity.RESULT_OK == resultCode && data != null) {
                    val selectedImage = AppUtils.flipping(data.extras!!["data"] as Bitmap)
                    imageView_product.setImageBitmap(selectedImage)
                    image = selectedImage
                }
            }
            1 -> {
                if (Activity.RESULT_OK == resultCode && data != null) {
                    val selectedImage: Uri? = data.data
                    val bitmap = AppUtils.getCorrectlyOrientedImage(
                        this,
                        selectedImage!!
                    )
                    imageView_product.setImageBitmap(bitmap)
                    image = bitmap
                }
            }
            2 -> {
                if (Activity.RESULT_OK == resultCode && data != null) {
                    val selectedImage = data.getStringExtra("productCode")
                    editText_code.setText(selectedImage)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
