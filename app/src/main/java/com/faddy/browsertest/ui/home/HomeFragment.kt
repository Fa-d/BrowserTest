package com.faddy.browsertest.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faddy.browsertest.R
import com.faddy.browsertest.databinding.FragmentHomeBinding
import com.faddy.browsertest.ui.history.OpenedTabsBottomSheet
import com.faddy.browsertest.utils.*
import com.faddy.browsertest.webViews.GenericWebView
import com.faddy.browsertest.webViews.GenericWebViewChromeClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.torproject.jni.TorService
import java.util.*
import java.util.regex.Pattern


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var genericContentFrame: FrameLayout
    private lateinit var genericWebView: WebView
    private var historyAdapter = HistoryAdapter()
    private var historyTextAdapter = HistoryTextAdapter()
    private var historyTempTextList = listOf<String>(
        "I want white teeth.",
        "Having a monkey is illegal.\n",
        "What a day we're having! her mother sighed.",
        "RANDOM SENTENCE GENERATOR\n",
        "Type of Sentence\n",
        " Sentences   Phrases   Questions\n",
        "Number of Sentences to generate\n",
        "Choose Length\n",
        "Here are 20 random sentences.\n",
        "Click or tap a sentence to bookmark or save.\n",
        "I want white teeth.\n",
        "Having a monkey is illegal.\n",
        "\"What a day we're having!\" her mother sighed.\n",
        "A balanced diet is a cookie in each hand.\n",
        "She’s an excellent photographer.\n",
        "We have group fitness classes.\n",
        "The big ugly tree destroys the beauty of the house.\n",
        "His looks are always funny.\n",
        "Tom took a big breath and blew out the candles.\n",
        "It’s difficult to say, but I think our customers are more satisfied.\n",
        "We have a big stove which keeps us very toasty.\n",
        "The pig put his snout through the fence.\n",
        "When people walk on the bridge, it shakes.\n",
        "I haven’t heard anything about him since you wrote to me.\n",
        "Big men are not necessarily strong men.\n",
        "I am so thankful for this opportunity.\n",
        "Sighing, the professor put on the pirate hat.\n",
        "Do you think you're a bigger man than him?\n",
        "What do you call that in English?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initService()
    }

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
        registerForContextMenu(binding.lastIcon)
        setHasOptionsMenu(true)
        registerForContextMenu(binding.theMainFrameLayout)
    }

    private fun menuController() {
        val operatorMenu = MenuOperator(requireContext(), genericWebView)
        binding.lastIcon.setOnClickListener {
            operatorMenu.showPopUp(binding.lastIcon)
        }
    }

    private fun initService() {
        activity?.bindService(
            Intent(requireContext(), TorService::class.java),
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    val torService = (service as TorService.LocalBinder).service
                    val conn = torService.torControlConnection
                    while ((conn == torService.torControlConnection) == null) {
                        try {
                            Thread.sleep(500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    if (conn != null) {
                        Toast.makeText(
                            requireContext(),
                            "Got Tor control connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            },
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }

    private fun initListeners() {
        binding.searchET.setOnFocusChangeListener { _, isSelected ->
            if (isSelected) {
                visibilityUnitController(true)
            }
        }
        binding.searchET.addTextChangedListener { _ ->
            val tempString = binding.searchET.text.toString().trim()
            if (tempString == "") binding.searchbarRecycler.visibility =
                View.GONE else binding.searchbarRecycler.visibility = View.VISIBLE
            val theFilteredResult =
                historyTempTextList.filter { listText -> listText.contains(tempString) }
            historyTextAdapter.initLoad(theFilteredResult)
        }
        //binding.theMainWebView.addView(WebView(requireContext()))
    }

    private fun visibilityUnitController(firstView: Boolean = false) {
        if (isWebViewInflated() || firstView) { //webview inflated so UI... searchbar should be at middle
            binding.guidelineInner.setGuidelinePercent(0.0f)
            showHistoryAndHideRecent(true)
            updateLayoutParamsOfSearchbar("min")

        } else { //visible webview so
            binding.guidelineInner.setGuidelinePercent(0.40f)
            showHistoryAndHideRecent(false)
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
        }
    }


    private fun initClick() {

        genericWebView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) {
                Toast.makeText(requireContext(), "Here is something 2", Toast.LENGTH_SHORT).show()
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (genericWebView.canGoBack()) {
                    genericWebView.goBack()
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
            binding.searchET.setText("")
            binding.searchET.clearFocus()
            binding.historyRecycler.visibility = View.VISIBLE
            binding.searchbarRecycler.visibility = View.GONE
            binding.cancelSearchButton.visibility = View.GONE
            hideKeyboard()
            visibilityUnitController()
        }
        binding.searchET.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (isURL(binding.searchET.text.trim().toString())) {
                    genericWebView.loadUrl(binding.searchET.text.trim().toString())
                } else {
                    genericWebView.loadUrl(
                        "https://www.duckduckgo.com/?q=${
                            binding.searchET.text.trim().toString().replace(" ", "+")
                        }"
                    )
                }
                /*viewModel.checkIfDataAlreadyExists(genericWebView.url ?: "")
                    .observe(viewLifecycleOwner, Observer { isTrue ->
                        if (isTrue) {
                            viewModel.getHitCountSingleSite(genericWebView.url ?: "")
                                .observe(viewLifecycleOwner, Observer { isFetchedCount ->
                                    if (isFetchedCount > 0) {
                                        viewModel.incrementHitCount(
                                            isFetchedCount + 1,
                                            genericWebView.url ?: ""
                                        ).observe(viewLifecycleOwner, Observer {
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
                                                generatedURL = genericWebView.url ?: "",
                                                title = binding.searchET.text.toString(),
                                                hitTimeStamp = Calendar.getInstance().timeInMillis,
                                                hitCount = 1
                                            )
                                        )
                                    }
                                })

                        } else {
                            viewModel.insertUrlIntoTable(
                                URLData(
                                    generatedURL = genericWebView.url ?: "",
                                    title = binding.searchET.text.toString(),
                                    hitTimeStamp = Calendar.getInstance().timeInMillis,
                                    hitCount = 1
                                )
                            )
                        }
                    })*/
                hideKeyboard()
                genericContentFrame.visibility = View.VISIBLE
                visibilityUnitController(true)
                binding.searchET.setText(genericWebView.url)
                binding.searchET.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
        genericContentFrame.setOnKeyListener { view, key, keyEvent ->
            if (key == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP && genericWebView.canGoBack()) {
                genericWebView.goBack()
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

    }

    private fun initData() {
        historyAdapter.initLoad(
            listOf<String>(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h",
                "i",
                "j",
                "k"
            )
        )
    }

    private fun initView() {
        genericWebView = GenericWebView(requireActivity()).initView()
        binding.searchProgress.visibility = View.VISIBLE
        genericContentFrame = binding.theMainFrameLayout
        genericContentFrame.removeAllViews()
        genericContentFrame.addView(genericWebView)
        val chromeClientInstance = GenericWebViewChromeClient()
        chromeClientInstance.progresse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == 0 || it == 100) {
                binding.searchProgress.visibility = View.GONE
            } else {
                binding.searchProgress.visibility = View.VISIBLE
            }
            binding.searchProgress.progress = it
        })
        genericWebView.webChromeClient = chromeClientInstance
        genericWebView.webViewClient = WebViewClient()

        /*binding.theMainWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        with(binding.theMainWebView) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            clearHistory()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
        }*/
        with(binding.historyRecycler) {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 3)
            adapter = historyAdapter
        }
        with(binding.searchbarRecycler) {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = historyTextAdapter
        }
        binding.firstButton.setOnClickListener {
            showPickupBottomSheet()
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val hitTestResult: WebView.HitTestResult = genericWebView.hitTestResult
        when (hitTestResult.type) {
            WebView.HitTestResult.ANCHOR_TYPE -> {
                Toast.makeText(requireContext(), "1", Toast.LENGTH_SHORT).show()
            }
            WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                val handler = Handler()
                val message = handler.obtainMessage()

                genericWebView.requestFocusNodeHref(message)
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
        return genericContentFrame.height > 0
    }

    private fun showHistoryAndHideRecent(flag: Boolean) {
        if (flag) {
            binding.historyRecycler.visibility = View.GONE
            binding.searchbarRecycler.visibility = View.VISIBLE
            binding.cancelSearchButton.visibility = View.VISIBLE
        } else {
            binding.historyRecycler.visibility = View.VISIBLE
            binding.searchbarRecycler.visibility = View.GONE
            binding.cancelSearchButton.visibility = View.GONE
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
        val tag: String = OpenedTabsBottomSheet.tag
        val dialog: OpenedTabsBottomSheet = OpenedTabsBottomSheet.newInstance(bundleOf())
        dialog.show(childFragmentManager, tag)
    }
}