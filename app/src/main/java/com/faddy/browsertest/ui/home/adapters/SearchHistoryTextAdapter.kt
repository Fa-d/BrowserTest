package com.faddy.browsertest.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.databinding.ItemViewHistoryRecyclerTextBinding
import com.faddy.browsertest.models.MostVisitedSitesModel

class SearchHistoryTextAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<MostVisitedSitesModel> = mutableListOf()
    var onItemClick: ((theFetchedUrl: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewHistoryRecyclerTextBinding = ItemViewHistoryRecyclerTextBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            binding.theInnerHistoryText.text = model.title
            binding.startImage.setImageBitmap(com.faddy.browsertest.utils.getBitmap(model.favIconBlob))
        }
    }

    internal inner class ViewModel(val binding: ItemViewHistoryRecyclerTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition].generatedURL)
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