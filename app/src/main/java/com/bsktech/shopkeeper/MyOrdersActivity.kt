package com.bsktech.shopkeeper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsktech.shopkeeper.adaptors.StoreOrdersAdapter
import com.bsktech.shopkeeper.models.Customer
import com.bsktech.shopkeeper.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.content_my_orders.*
import java.util.*
import kotlin.collections.HashMap

class MyOrdersActivity : AppCompatActivity(), (Customer, Order) -> Unit {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var listAdapter: StoreOrdersAdapter
    private val barcodeFieldList = ArrayList<Order>()
    private val customers = HashMap<String, Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView_orders.apply {
            layoutManager = LinearLayoutManager(this@MyOrdersActivity)
            listAdapter = StoreOrdersAdapter(barcodeFieldList, customers, this@MyOrdersActivity)
            adapter = listAdapter
        }

        getAllOrders()
    }

    private fun getAllOrders() {
        db.collection("orders").whereEqualTo("storeId", auth.currentUser?.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                barcodeFieldList.clear()

                for (document in value!!) {
                    val storeItemCart = document.toObject(Order::class.java)
                    storeItemCart._id = document.id
                    barcodeFieldList.add(storeItemCart)
                }

                getAllCustomers(barcodeFieldList);

                listAdapter.notifyDataSetChanged()
            }
    }

    private fun getAllCustomers(orders: ArrayList<Order>) {
        for (order in orders) {
            if (!customers.containsKey(order.uid)) {
                db.collection("customers").document(order.uid!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val customerData = document.toObject(Customer::class.java)
                            customerData?._id = document.id
                            customers[document.id] = customerData!!
                            listAdapter.notifyDataSetChanged()
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
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

    companion object {
        private const val TAG = "MyOrdersActivity"
    }

    override fun invoke(customer: Customer, order: Order) {
        Log.d(TAG, order._id)
        val intent = Intent(this, MyOrderSummeryActivity::class.java);
        intent.putExtra("customer", customer)
        intent.putExtra("order", order)
        startActivity(intent)
    }
}
