package com.faddy.browsertest.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.faddy.browsertest.R
import com.faddy.browsertest.databinding.FragmentOpenedTabsBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class OpenedTabsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOpenedTabsBottomsheetBinding
    private lateinit var theAdapter: OpenedTabsAdapter
    var onNewTabClicked: ((isAgreed: Boolean) -> Unit)? = null
    var onTabDeleteClicked: ((isAgreed: Int) -> Unit)? = null
    var onTabSelected: ((isAgreed: Int, theTabTitle: String) -> Unit)? = null
    var isDismissed: ((dismissed: Boolean) -> Unit)? = null
    var passedDatalist = arrayListOf<String>()

    companion object {
        fun newInstance(bundle: Bundle?): OpenedTabsBottomSheet = OpenedTabsBottomSheet().apply {
            passedDatalist = bundle?.getStringArrayList("newList") ?: arrayListOf()
        }

        val tag: String = OpenedTabsBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding.root.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                skipCollapsed = true
                isHideable = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentOpenedTabsBottomsheetBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initClick()
    }

    private fun initData() {
        theAdapter = OpenedTabsAdapter()
        theAdapter.initLoad(passedDatalist)
    }

    private fun initClick() {
        binding.openNewTab.setOnClickListener {
            onNewTabClicked?.invoke(true)
            dismiss()
        }
        theAdapter.onTabSelect = { index, tabTitle ->
            onTabSelected?.invoke(index, tabTitle)
        }
        theAdapter.closeTab = { index ->
            onTabDeleteClicked?.invoke(index)
        }
    }

    private fun initView() {
        with(binding.openedItemsRecycler) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = theAdapter
        }
    }

    override fun onDestroyView() {
        isDismissed?.invoke(true)
        super.onDestroyView()
    }
}