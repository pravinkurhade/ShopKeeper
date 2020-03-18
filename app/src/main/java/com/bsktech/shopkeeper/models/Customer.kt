package com.bsktech.shopkeeper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customer(
    var _id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var image: String? = null,
    var loginType: String? = null,
    var timestamp: Long? = null
) : Parcelable
