package com.faddy.browsertest.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.marginEnd
import androidx.core.view.size
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.R
import com.faddy.browsertest.databinding.FragmentHomeBinding
import com.faddy.browsertest.models.MostVisitedSitesModel
import com.faddy.browsertest.models.NewTabsModel
import com.faddy.browsertest.models.URLData
import com.faddy.browsertest.ui.home.adapters.DialogueOverflowAdapter
import com.faddy.browsertest.ui.opened_tabs.OpenedTabsBottomSheet
import com.faddy.browsertest.utils.*
import com.faddy.browsertest.webViews.GenericWebView
import com.faddy.browsertest.webViews.GenericWebViewChromeClient
import com.faddy.browsertest.webViews.GenericWebViewClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Pattern


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeBinding.inflate(inflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
        initListeners()
        initData()
        menuController()
        registerForContextMenu(binding.menuButtonMain)
        setHasOptionsMenu(true)
        registerForContextMenu(binding.theMainFrameLayout)
    }

    private fun menuController() {
        val operatorMenu = MenuOperator(requireContext(), viewModel.genericWebView)
        binding.menuButtonMain.setOnClickListener {
            operatorMenu.showPopUp(binding.menuButtonMain)
        }
        operatorMenu.closeApp = {
            activity?.finish()
        }
        operatorMenu.openNewTab = {
            showPickupBottomSheet()
        }
    }

    private fun initListeners() {
        binding.searchET.setOnFocusChangeListener { _, isSelected ->
            if (isSelected) {
                visibilityUnitController(true)
            }
        }
        binding.searchET.addTextChangedListener { _ ->
            val currentSearchString = binding.searchET.text.toString().trim()
            binding.searchSuggestionRecycler.visibility =
                if (currentSearchString == "") View.GONE else View.VISIBLE
            val theFilteredResult = mutableListOf<MostVisitedSitesModel>()
            viewModel.searchbarItemDataList.forEach { data ->
                if (data.title.contains(currentSearchString)) theFilteredResult.add(data)
            }
            viewModel.searchHistoryTextAdapter.initLoad(theFilteredResult)
        }
    }

    private fun visibilityUnitController(
        isSearchETSelected: Boolean = false,
        isNotHomeButtonPressed: Boolean = true
    ) {
        if ((isWebViewInflated() || isSearchETSelected) && isNotHomeButtonPressed) { //webview inflated so UI... searchbar should be at middle
            binding.guidelineInner.setGuidelinePercent(0.0f)
            showFrequentlyVisitedAndHideSearchSuggestions(true)
            updateLayoutParamsOfSearchbar("min")

        } else { //visible webview so
            binding.guidelineInner.setGuidelinePercent(0.40f)
            showFrequentlyVisitedAndHideSearchSuggestions(false)
            updateLayoutParamsOfSearchbar("max")
        }
    }

    private fun updateLayoutParamsOfSearchbar(flag: String) {
        if (flag == "min") {
            binding.serachLT.updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToStart = binding.guideline80.id
                startToEnd = binding.guideline1.id
                endToEnd = ConstraintLayout.LayoutParams.UNSET
                startToStart = ConstraintLayout.LayoutParams.UNSET
                topMargin = 10
                marginEnd = 0
                marginStart = 0
                width = 0
            }
            binding.tabcountButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToTop = binding.root.top
                bottomToBottom = binding.root.bottom
            }
            binding.menuButtonMain.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToTop = binding.root.top
                endToEnd = binding.root.marginEnd
                bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
        } else if (flag == "max") {
            binding.serachLT.updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToStart = ConstraintLayout.LayoutParams.UNSET
                startToEnd = ConstraintLayout.LayoutParams.UNSET
                endToEnd = binding.root.id
                startToStart = binding.root.id
                topMargin = 0
                marginEnd = 10
                marginStart = 10
                width = ConstraintLayout.LayoutParams.MATCH_PARENT
            }
            binding.tabcountButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToTop = binding.searchET.top
                bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            binding.menuButtonMain.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToTop = binding.tabcountButton.top
                bottomToBottom = binding.tabcountButton.bottom
            }
        }
    }


    private fun initClick() {

        viewModel.genericWebView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) {
                Toast.makeText(requireContext(), "Here is something 2", Toast.LENGTH_SHORT).show()
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (viewModel.genericWebView.canGoBack()) {
                    viewModel.genericWebView.goBack()
                    return@OnKeyListener true
                } else {
                    (activity)?.onBackPressed()
                }
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                Toast.makeText(requireContext(), "Here is something", Toast.LENGTH_SHORT).show()
            }
            return@OnKeyListener false
        })

        binding.cancelSearchButton.setOnClickListener {
            hideKeyboardAndUnfocus()
            binding.frequentlyVisitedRecyclerView.visibility = View.VISIBLE
            binding.searchSuggestionRecycler.visibility = View.GONE
            binding.cancelSearchButton.visibility = View.GONE
            visibilityUnitController()
        }
        binding.searchET.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isURL(binding.searchET.text.trim().toString())) {
                    viewModel.genericWebView.loadUrl(binding.searchET.text.trim().toString())
                } else {
                    viewModel.genericWebView.loadUrl(
                        "https://www.duckduckgo.com/?q=${
                            binding.searchET.text.trim().toString().replace(" ", "+")
                        }"
                    )
                }
                viewModel.genericContentFrame.visibility = View.VISIBLE
                visibilityUnitController(true)
                hideKeyboardAndUnfocus()
                binding.searchET.setText(viewModel.genericWebView.url)
                return@OnEditorActionListener true
            }
            false
        })
        viewModel.genericContentFrame.setOnKeyListener { view, key, keyEvent ->
            if (key == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP && viewModel.genericWebView.canGoBack()) {
                viewModel.genericWebView.goBack()
            }
            if (key == KeyEvent.KEYCODE_MENU) {
                Toast.makeText(
                    requireContext(),
                    "Hey There",
                    Toast.LENGTH_LONG
                ).show()
            }
            false
        }

        binding.homeIcon.setOnClickListener { state0() }
        viewModel.searchHistoryTextAdapter.onItemClick = { theFetchedUrl ->
            loadUrlOnClick(theFetchedUrl)
        }
        viewModel.mostVisitedSitesAdapter.onItemClick = { theFetchedUrl ->
            visibilityUnitController(true)
            loadUrlOnClick(theFetchedUrl)
        }
    }

    private fun loadUrlOnClick(theFetchedUrl: String) {
        viewModel.genericWebView.loadUrl(theFetchedUrl)
        hideKeyboardAndUnfocus()
        binding.searchET.setText(theFetchedUrl)
        binding.frequentlyVisitedRecyclerView.visibility = View.GONE
        binding.searchSuggestionRecycler.visibility = View.GONE
        binding.cancelSearchButton.visibility = View.VISIBLE
        binding.theMainFrameLayout.visibility = View.VISIBLE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
    }

    /**
     * hidekeyboard
     * unfocus edittext
     * */

    private fun hideKeyboardAndUnfocus() {
        binding.searchET.setText("")
        binding.searchET.clearFocus()
        hideKeyboard()
    }

    private fun showKeyboardAndFocus() {
        binding.searchET.setText("")
        binding.searchET.requestFocus()
    }


    private fun state0() {
        //todo
        /**
         * Drag searchbar to middle (compute the size)
         * show frequently visited sites
         * hide search recycler
         * update tab count
         * reset webview to gone clear url
         *
         */
        visibilityUnitController(isSearchETSelected = false, isNotHomeButtonPressed = false)
        hideKeyboardAndUnfocus()
    }

    fun stateSearch1Webview0() {

    }

    private fun initData() {
        viewModel.getTitleURLImageFromDB()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { datalist ->
                viewModel.mostVisitedSitesAdapter.initLoad(datalist)
            })
        viewModel.getTitleURLImageFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.searchbarItemDataList.addAll(it)
        })
    }

    fun webViewInitializer() {
        viewModel.genericWebView = GenericWebView(requireActivity()).initView()
        val chromeClientInstance = GenericWebViewChromeClient()
        chromeClientInstance.onFavIconRecieved = { icon ->
            Log.d("THeDebugggingIcon 21", "$icon")
            if (icon == null) {
                Toast.makeText(
                    requireContext(), "Icon is null", Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.setFavionToDB(imageToBitmap(icon), viewModel.genericWebView.url!!)
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it) {
                            Toast.makeText(
                                requireContext(), "Succesfully inserted", Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
        chromeClientInstance.progresse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.searchProgress.visibility =
                if (it == 0 || it == 100) {
                    binding.swipeRefreshLayout.isRefreshing = false;View.GONE
                } else View.VISIBLE
            binding.searchProgress.progress = it
        })
        viewModel.genericWebView.webChromeClient = chromeClientInstance
        val genericWebViewInstance = GenericWebViewClient()
        genericWebViewInstance.onTitleRecieved = {
            saveInformationsToLocalDB()
            viewModel.setTitleOfUrl(it, viewModel.genericWebView.url ?: "")
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer { isUpdatedTitle ->
                    if (isUpdatedTitle) Toast.makeText(
                        requireContext(), "title Updated", Toast.LENGTH_SHORT
                    ).show()
                })
        }
        genericWebViewInstance.refreshUrlTitle = { newURL ->
            binding.searchET.setText(newURL)
        }
        viewModel.genericWebView.webViewClient = genericWebViewInstance

        viewModel.genericWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        with(viewModel.genericWebView) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            clearHistory()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
        }
        viewModel.genericContentFrame.addView(viewModel.genericWebView)
    }

    private fun saveInformationsToLocalDB() {
        viewModel.checkIfDataAlreadyExists(viewModel.genericWebView.url ?: "")
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { isTrue ->
                if (isTrue) {
                    viewModel.getHitCountSingleSite(viewModel.genericWebView.url ?: "")
                        .observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { isFetchedCount ->
                                if (isFetchedCount > 0) {
                                    viewModel.incrementHitCount(
                                        isFetchedCount + 1,
                                        viewModel.genericWebView.url ?: ""
                                    ).observe(
                                        viewLifecycleOwner,
                                        androidx.lifecycle.Observer {
                                            if (it) {
                                                Log.d(
                                                    "TheTad",
                                                    "Suggessfull inserted new URL Into Database"
                                                )
                                            }
                                        })
                                } else {
                                    viewModel.insertUrlIntoTable(
                                        URLData(
                                            generatedURL = viewModel.genericWebView.url ?: "",
                                            title = viewModel.genericWebView.title ?: "",
                                            hitTimeStamp = Calendar.getInstance().timeInMillis,
                                            hitCount = 1,
                                            favIconBlob = ByteArray(0)
                                        )
                                    )
                                }
                            })
                } else {
                    viewModel.insertUrlIntoTable(
                        URLData(
                            generatedURL = viewModel.genericWebView.url ?: "",
                            title = viewModel.genericWebView.title ?: "",
                            hitTimeStamp = Calendar.getInstance().timeInMillis,
                            hitCount = 1,
                            favIconBlob = ByteArray(0)
                            //favIconBlob = imageToBitmap(genericWebView.favicon!!)
                        )
                    )
                }
            })
    }


    private fun initView() {
        binding.tabcountButton.text = 1.toString()
        viewModel.genericWebView = GenericWebView(requireActivity()).initView()
        binding.searchProgress.visibility = View.VISIBLE
        viewModel.genericContentFrame = binding.theMainFrameLayout
        viewModel.genericContentFrame.addView(viewModel.genericWebView, 0)
        val chromeClientInstance = GenericWebViewChromeClient()
        chromeClientInstance.progresse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == 0 || it == 100) {
                binding.swipeRefreshLayout.isRefreshing = false;
                binding.searchProgress.visibility = View.GONE
            } else {
                binding.searchProgress.visibility = View.VISIBLE
            }
            binding.searchProgress.progress = it
        })
        viewModel.genericWebView.webChromeClient = chromeClientInstance
        chromeClientInstance.onFavIconRecieved = { icon ->
            if (icon == null) {
                Toast.makeText(requireContext(), "Icon Null", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.setFavionToDB(imageToBitmap(icon), viewModel.genericWebView.url ?: "")
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it) {
                            Toast.makeText(
                                requireContext(), "Succesfully inserted", Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
        val genericWebViewInstance = GenericWebViewClient()
        genericWebViewInstance.onTitleRecieved = {
            saveInformationsToLocalDB()
            viewModel.setTitleOfUrl(it, viewModel.genericWebView.url ?: "")
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer { isUpdatedTItle ->
                    if (isUpdatedTItle) Toast.makeText(
                        requireContext(), "title Updated", Toast.LENGTH_SHORT
                    ).show()
                })
        }
        genericWebViewInstance.refreshUrlTitle = { newURL ->
            binding.searchET.setText(newURL)
        }
        viewModel.genericWebView.webViewClient = genericWebViewInstance

        viewModel.genericWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        with(viewModel.genericWebView) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            clearHistory()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
        }
        with(binding.frequentlyVisitedRecyclerView) {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 3)
            adapter = viewModel.mostVisitedSitesAdapter
        }
        with(binding.searchSuggestionRecycler) {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = viewModel.searchHistoryTextAdapter
        }
        binding.tabcountButton.setOnClickListener {
            showPickupBottomSheet()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            Toast.makeText(requireContext(), "swipeRefreshed", Toast.LENGTH_SHORT).show()
            viewModel.genericWebView.reload()
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val hitTestResult: WebView.HitTestResult = viewModel.genericWebView.hitTestResult
        when (hitTestResult.type) {
            WebView.HitTestResult.ANCHOR_TYPE -> {
                Toast.makeText(requireContext(), "1", Toast.LENGTH_SHORT).show()
            }
            WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                val handler = Handler()
                val message = handler.obtainMessage()
                viewModel.genericWebView.requestFocusNodeHref(message)
                val url = message.data.getString("url")
                showETCPopUp(requireContext().applicationContext, url)
                Toast.makeText(requireContext(), "2", Toast.LENGTH_SHORT).show()
            }
            WebView.HitTestResult.IMAGE_TYPE -> {
                Toast.makeText(requireContext(), "3", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), "4", Toast.LENGTH_SHORT).show()
            }
        }

        super.onCreateContextMenu(menu, v, menuInfo)
    }


    private fun isWebViewInflated(): Boolean {
        return viewModel.genericContentFrame.height > 0
    }

    private fun showFrequentlyVisitedAndHideSearchSuggestions(flag: Boolean) {
        if (flag) {
            binding.frequentlyVisitedRecyclerView.visibility = View.GONE
            binding.searchSuggestionRecycler.visibility = View.VISIBLE
            binding.cancelSearchButton.visibility = View.VISIBLE
            binding.theMainFrameLayout.visibility = View.VISIBLE
            binding.swipeRefreshLayout.visibility = View.VISIBLE
        } else {
            binding.frequentlyVisitedRecyclerView.visibility = View.VISIBLE
            binding.searchSuggestionRecycler.visibility = View.GONE
            binding.cancelSearchButton.visibility = View.GONE
            binding.theMainFrameLayout.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.GONE
        }
    }

    fun isURL(url: String): Boolean {
        var url = url
        url = url.lowercase(Locale.getDefault())
        if (url.startsWith(URL_ABOUT_BLANK)
            || url.startsWith(URL_SCHEME_FILE)
            || url.startsWith(URL_SCHEME_HTTP)
            || url.startsWith(URL_SCHEME_HTTPS)
            || url.startsWith(URL_SCHEME_FTP)
            || url.startsWith(URL_SCHEME_INTENT)
        ) {
            return true
        }
        val regex = ("^((ftp|http|https|intent)?://)" // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL -> 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?" // 端口 -> :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$")
        val pattern = Pattern.compile(regex)
        return pattern.matcher(url).matches()
    }

    private fun showETCPopUp(context: Context, url: String?) {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_overflow, null)
        val builder = MaterialAlertDialogBuilder(requireActivity()).setView(view)
        with(view.findViewById<RecyclerView>(R.id.overFlowItemRecycler)) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = DialogueOverflowAdapter().apply {
                initLoad(
                    listOf(
                        "Open in a new tab",
                        "Save Bookmark",
                        "Share Link",
                        "Copy link to clipboard",
                        "Download",
                        "Reload tab",
                        "Open Settings"
                    )
                )
            }
        }
        view.findViewById<TextView>(R.id.urlText)?.text = url
        val dialog = builder.create()
        dialog.window?.attributes?.width =
            (getDeviceMetrics(context).widthPixels.times(0.80)).toInt()
        dialog.show()
    }

    private fun getDeviceMetrics(context: Context): DisplayMetrics {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        display.getMetrics(metrics)
        return metrics
    }

    private fun showPickupBottomSheet() {
        if (viewModel.genericContentFrame.childCount > 0) {
            viewModel.newTabsTempList.clear()
        }
        viewModel.savedTabsInfo.clear()
        val newTempLis = mutableListOf<NewTabsModel>()
        for (i: Int in 0 until (viewModel.genericContentFrame.size)) {
            // newTabsTempList.add((genericContentFrame.getChildAt(i) as WebView).title.toString())
            newTempLis.add(
                NewTabsModel(
                    (viewModel.genericContentFrame.getChildAt(i) as WebView).title.toString(),
                    // (genericContentFrame.getChildAt(i) as WebView)
                )
            )
        }

        viewModel.savedTabsInfo.addAll(newTempLis)
        val tag: String = OpenedTabsBottomSheet.tag
        val dialog: OpenedTabsBottomSheet =
            OpenedTabsBottomSheet.newInstance(
                bundleOf()
                //   bundleOf("newList" to newTabsTempList as ArrayList<String>)
            )
        dialog.show(childFragmentManager, tag)

        dialog.onNewTabClicked = {
            if (it) {
                webViewInitializer()
                showKeyboardAndFocus()
            }
        }
        dialog.onTabSelected = { _, tabTitle ->
            for (i: Int in 0 until (viewModel.genericContentFrame.size)) {
                if ((viewModel.genericContentFrame.getChildAt(i) as WebView).title.toString() == tabTitle) {
                    viewModel.genericContentFrame[i].bringToFront()
                }
            }
        }
        dialog.onTabDeleteClicked = {
            viewModel.genericContentFrame.removeViewAt(it)
        }
        dialog.isDismissed = {
            if (it) {
                binding.tabcountButton.text = viewModel.genericContentFrame.childCount.toString()
            }
        }
    }
}