package com.bsktech.shopkeeper.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bsktech.shopkeeper.R
import com.bsktech.shopkeeper.models.MainMenu

internal class StoreListAdaptor(
    private val barcodeFieldList: List<MainMenu>,
    private val clickListener: (MainMenu) -> Unit
) :
    RecyclerView.Adapter<StoreListAdaptor.StoreListViewHolder>() {

    internal class StoreListViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.textView_title)
        private val subtitle: TextView = view.findViewById(R.id.textView_subtitle)

        fun bindBarcodeField(
            barcodeField: MainMenu,
            clickListener: (MainMenu) -> Unit
        ) {
            title.text = barcodeField.title
            subtitle.text = barcodeField.subtitle

            itemView.setOnClickListener { clickListener(barcodeField)}

        }

        companion object {
            fun create(parent: ViewGroup): StoreListViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_store_list_view, parent, false)
                return StoreListViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListViewHolder =
        StoreListViewHolder.create(parent)

    override fun onBindViewHolder(holder: StoreListViewHolder, position: Int) =
        holder.bindBarcodeField(barcodeFieldList[position], clickListener)

    override fun getItemCount(): Int =
        barcodeFieldList.size
}