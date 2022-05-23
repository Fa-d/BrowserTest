package com.faddy.browsertest.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.databinding.ItemViewHistoryRecyclerTextBinding

class HistoryTextAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

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
            binding.theInnerHistoryText.text = model
        }
    }

    internal inner class ViewModel(val binding: ItemViewHistoryRecyclerTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke()
                }
            }
        }
    }

    fun initLoad(list: List<String>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<String>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}