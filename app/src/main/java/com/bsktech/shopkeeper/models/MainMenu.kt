package com.bsktech.shopkeeper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainMenu(
    var title: String? = null,
    var subtitle: String? = null
) : Parcelable