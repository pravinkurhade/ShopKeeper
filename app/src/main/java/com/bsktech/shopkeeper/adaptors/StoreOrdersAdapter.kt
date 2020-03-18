/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bsktech.shopkeeper.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bsktech.shopkeeper.MyOrdersActivity
import com.bsktech.shopkeeper.R
import com.bsktech.shopkeeper.models.Customer
import com.bsktech.shopkeeper.models.Order
import com.bsktech.shopkeeper.utils.AppUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/** Presents a list of field info in the detected barcode.  */
internal class StoreOrdersAdapter(
    private val barcodeFieldList: List<Order>,
    private val customers: HashMap<String, Customer>,
    private val clickListener: MyOrdersActivity
) :
    RecyclerView.Adapter<StoreOrdersAdapter.BarcodeFieldViewHolder>() {

    internal class BarcodeFieldViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.textView_name)
        private val price: TextView = view.findViewById(R.id.textView_price)
        private val size: TextView = view.findViewById(R.id.textView_size)
        private val code: TextView = view.findViewById(R.id.textView_code)

        fun bindBarcodeField(
            barcodeField: Order,
            customers: HashMap<String, Customer>,
            clickListener: (Customer, Order) -> Unit
        ) {
            if (customers.containsKey(barcodeField.uid)) {
                val cus = customers[barcodeField.uid]
                name.text = "Order ID - " + barcodeField._id
                size.text = "Date - " + AppUtils.convertLongToTime(barcodeField.timestamp!!)
                price.text = "Total - ₹ " + barcodeField.total
                code.text = "Customer Name - " + cus?.name ?: ""
            }
            itemView.setOnClickListener {
                val cus = customers[barcodeField.uid]
                clickListener(cus!!, barcodeField)
            }
        }

        companion object {
            fun create(parent: ViewGroup): BarcodeFieldViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_order_item, parent, false)
                return BarcodeFieldViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeFieldViewHolder =
        BarcodeFieldViewHolder.create(parent)

    override fun onBindViewHolder(holder: BarcodeFieldViewHolder, position: Int) =
        holder.bindBarcodeField(barcodeFieldList[position], customers, clickListener)

    override fun getItemCount(): Int =
        barcodeFieldList.size
}
