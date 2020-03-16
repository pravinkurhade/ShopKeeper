package com.bsktech.shopkeeper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsktech.shopkeeper.adaptors.StoreCartItemsAdapter
import com.bsktech.shopkeeper.models.StoreItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_my_products.*
import kotlinx.android.synthetic.main.content_my_products.*
import java.util.ArrayList

class MyProductsActivity : AppCompatActivity(), (StoreItem) -> Unit {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var listAdapter: StoreCartItemsAdapter
    private val barcodeFieldList = ArrayList<StoreItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_products)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fab.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        recyclerView_products.apply {
            layoutManager = LinearLayoutManager(this@MyProductsActivity)
            listAdapter = StoreCartItemsAdapter(barcodeFieldList, this@MyProductsActivity)
            adapter = listAdapter
        }

        getAllCartItems()
    }

    private fun getAllCartItems() {
        db.collection("StoreItems").whereEqualTo("storeId", auth.currentUser?.uid).addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                barcodeFieldList.clear()

                for (document in value!!) {
                    val storeItemCart = document.toObject(StoreItem::class.java)
                    storeItemCart._id = document.id
                    barcodeFieldList.add(storeItemCart)
                }

                listAdapter.notifyDataSetChanged()
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

    companion object {
        private const val TAG = "MyProductsActivity"
    }

    override fun invoke(storeItem: StoreItem) {
        val intent = Intent(this, AddProductActivity::class.java);
        intent.putExtra("storeItem", storeItem)
        startActivity(intent)
    }

}
