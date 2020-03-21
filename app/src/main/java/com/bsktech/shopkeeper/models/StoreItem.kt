package com.bsktech.shopkeeper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreItem(
    var _id: String? = null,
    var name: String? = null,
    var productCode: String? = null,
    var size: String? = null,
    var storeId: String? = null,
    var price: Double? = null,
    var image: ArrayList<String>? = null,
    var productCodeStoreId: String? = null,
    var timestamp: Long? = null,
    var uid: String? = null,
    var cartItemId: String? = null,
    var quantity: Int? = null
) : Parcelable
