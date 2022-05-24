package com.faddy.browsertest.ui.home;

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.faddy.browsertest.databinding.FragmentHomeBinding
import com.faddy.browsertest.models.URLData
import com.faddy.browsertest.utils.*
import com.faddy.browsertest.webViewClient.GenericWebViewClient
import dagger.hilt.android.AndroidEntryPoint
import org.torproject.jni.TorService
import java.util.*
import java.util.regex.Pattern

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
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
        initService()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeBinding.inflate(inflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClickListener()
        initListeners()
        initData()
    }

    private fun initService() {
        activity?.bindService(
            Intent(requireContext(), TorService::class.java),
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    val torService = (service as TorService.LocalBinder).service
                    var conn = torService.torControlConnection
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
                width = 0
            }
        } else if (flag == "max") {
            binding.serachLT.updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToStart = ConstraintLayout.LayoutParams.UNSET
                startToEnd = ConstraintLayout.LayoutParams.UNSET
                endToEnd = binding.root.id
                startToStart = binding.root.id
                topMargin = 0
                width = ConstraintLayout.LayoutParams.MATCH_PARENT
            }
        }
    }


    private fun initClickListener() {
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
                binding.theMainWebView.settings.javaScriptEnabled = true
                binding.theMainWebView.settings.domStorageEnabled = true
                if (isURL(binding.searchET.text.trim().toString())) {
                    binding.theMainWebView.loadUrl(binding.searchET.text.trim().toString())
                } else {
                    binding.theMainWebView.loadUrl(
                        "https://www.google.com/search?q=${
                            binding.searchET.text.trim().toString().replace(" ", "+")
                        }"
                    )
                }
                viewModel.checkIfDataAlreadyExists(binding.theMainWebView.url ?: "")
                    .observe(viewLifecycleOwner, Observer { isTrue ->
                        if (isTrue) {
                            viewModel.getHitCountSingleSite(binding.theMainWebView.url ?: "")
                                .observe(viewLifecycleOwner, Observer { isFetchedCount ->
                                    if (isFetchedCount > 0) {
                                        viewModel.incrementHitCount(
                                            isFetchedCount + 1,
                                            binding.theMainWebView.url ?: ""
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
                                                generatedURL = binding.theMainWebView.url ?: "",
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
                                    generatedURL = binding.theMainWebView.url ?: "",
                                    title = binding.searchET.text.toString(),
                                    hitTimeStamp = Calendar.getInstance().timeInMillis,
                                    hitCount = 1
                                )
                            )
                        }
                    })
                binding.theMainWebView.visibility = View.VISIBLE
                hideKeyboard()
                visibilityUnitController(true)
                binding.searchET.setText(binding.theMainWebView.url)
                binding.searchET.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
        binding.theMainWebView.setOnKeyListener { view, key, keyEvent ->
            if (key == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP && binding.theMainWebView.canGoBack()) {
                binding.theMainWebView.goBack()
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
        binding.theMainWebView.webViewClient = GenericWebViewClient(binding.theMainWebView)
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
    }

    private fun isWebViewInflated(): Boolean {
        return binding.theMainWebView.height > 0
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
}