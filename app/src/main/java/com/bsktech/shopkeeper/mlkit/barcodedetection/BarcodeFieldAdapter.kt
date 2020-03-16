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

package com.bsktech.shopkeeper.mlkit.barcodedetection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bsktech.shopkeeper.R
import com.bsktech.shopkeeper.mlkit.barcodedetection.BarcodeFieldAdapter.BarcodeFieldViewHolder
import com.bsktech.shopkeeper.models.StoreItem
import com.bsktech.shopkeeper.utils.AppUtils
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

/** Presents a list of field info in the detected barcode.  */
internal class BarcodeFieldAdapter(
    private val barcodeFieldList: List<StoreItem>,
    private val clickListener: (StoreItem) -> Unit
) :
    RecyclerView.Adapter<BarcodeFieldViewHolder>() {

    internal class BarcodeFieldViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.textView_name)
        private val price: TextView = view.findViewById(R.id.textView_price)
        private val size: TextView = view.findViewById(R.id.textView_size)
        private val code: TextView = view.findViewById(R.id.textView_code)
        private val imageView: ImageView = view.findViewById(R.id.imageView_image)

        fun bindBarcodeField(
            barcodeField: StoreItem,
            clickListener: (StoreItem) -> Unit
        ) {
            name.text = AppUtils.toCamalCase(barcodeField.name)
            size.text = barcodeField.size
            price.text = "₹ " + barcodeField.price
            code.text = "EAN code : " + barcodeField.productCode

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val pathReference = storageRef.child(barcodeField.image!![0])

            Glide.with(imageView.context)
                .load(pathReference)
                .into(imageView)
        }

        companion object {
            fun create(parent: ViewGroup): BarcodeFieldViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.barcode_field, parent, false)
                return BarcodeFieldViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeFieldViewHolder =
        BarcodeFieldViewHolder.create(parent)

    override fun onBindViewHolder(holder: BarcodeFieldViewHolder, position: Int) =
        holder.bindBarcodeField(barcodeFieldList[position], clickListener)

    override fun getItemCount(): Int =
        barcodeFieldList.size
}
