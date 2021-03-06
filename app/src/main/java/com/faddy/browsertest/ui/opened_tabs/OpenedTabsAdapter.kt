package com.faddy.browsertest.ui.opened_tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.databinding.ItemViewOpenedTabBinding
import com.faddy.browsertest.models.NewTabsModel

class OpenedTabsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<NewTabsModel> = mutableListOf()
    var closeTab: ((index: Int) -> Unit)? = null
    var onTabSelect: ((index: Int, theTabTitle: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewOpenedTabBinding = ItemViewOpenedTabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding
            binding.urlText.text = model.theTitle
            /*binding.fittingView.apply {
                if (model.theView != null) {
                    this.removeAllViews()
                    this.addView(model.theView)
                }
            }*/
        }
    }

    internal inner class ViewHolder(val binding: ItemViewOpenedTabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.closeImage.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    closeTab?.invoke(adapterPosition)
                    dataList.removeAt(adapterPosition)
                    notifyDataSetChanged()
                }
            }
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onTabSelect?.invoke(adapterPosition, binding.urlText.text.toString())
                }
            }
        }
    }

    fun initLoad(list: List<NewTabsModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}