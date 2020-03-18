package com.bsktech.shopkeeper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

/** Information about a barcode field.  */
@Parcelize
data class Order(
    var _id: String? = null,
    var uid: String? = null,
    var storeId: String? = null,
    var uidStoreId: String? = null,
    var orderItems: ArrayList<StoreItem>? = null,
    var total: Double? = null,
    var paymentMode: String? = "Cash",
    var paymentRefNo: String? = null,
    var transactionObject: String? = null,
    var timestamp: Long? = null
) : Parcelable
