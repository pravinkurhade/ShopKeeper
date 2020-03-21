package com.bsktech.shopkeeper

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsktech.shopkeeper.adaptors.OrderSummaryItemsAdapter
import com.bsktech.shopkeeper.models.Customer
import com.bsktech.shopkeeper.models.Order
import com.bsktech.shopkeeper.models.StoreItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_order_summery.*
import kotlinx.android.synthetic.main.content_my_order_summery.*
import java.util.ArrayList

class MyOrderSummeryActivity : AppCompatActivity(), (StoreItem) -> Unit {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var listAdapter: OrderSummaryItemsAdapter
    private var orderItems = ArrayList<StoreItem>()

    private var customer = Customer()
    private var order = Order()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_summery)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (intent.extras != null) {
            customer = intent.getParcelableExtra<Customer>("customer")
            order = intent.getParcelableExtra<Order>("order")
            orderItems = order.orderItems!!
            setTotal()
        }

        recyclerView_order_summary_items.apply {
            layoutManager = LinearLayoutManager(this@MyOrderSummeryActivity)
            listAdapter = OrderSummaryItemsAdapter(orderItems, this@MyOrderSummeryActivity)
            adapter = listAdapter
        }
    }

    private fun setTotal() {
        textView_total.text = "Total ₹ ${order.total}"
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

    override fun invoke(p1: StoreItem) {
        TODO("Not yet implemented")
    }
}
