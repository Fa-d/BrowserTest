package com.faddy.browsertest.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.drawable.toIcon
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.databinding.ItemViewHistoryRecyclerBinding
import com.faddy.browsertest.models.MostVisitedSitesModel
import com.faddy.browsertest.utils.getBitmap

class MostVisitedSitesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<MostVisitedSitesModel> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewHistoryRecyclerBinding = ItemViewHistoryRecyclerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding
            binding.nameOfWebsite.text = model.siteName
            binding.webIcon.setImageBitmap( getBitmap(model.favIconBlob))
        }
    }

    internal inner class ViewHolder(val binding: ItemViewHistoryRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke()
                }
            }
        }
    }

    fun initLoad(list: List<MostVisitedSitesModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}