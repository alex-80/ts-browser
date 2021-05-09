package com.hinnka.tsbrowser.ui.home

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.Browser
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hinnka.tsbrowser.BuildConfig
import com.hinnka.tsbrowser.persist.Bookmark
import com.hinnka.tsbrowser.ext.logD
import com.hinnka.tsbrowser.ext.toUrl
import com.hinnka.tsbrowser.tab.TabManager
import com.hinnka.tsbrowser.tab.active
import com.hinnka.tsbrowser.ui.base.BaseActivity
import com.hinnka.tsbrowser.ui.composable.bookmark.AddFolder
import com.hinnka.tsbrowser.ui.composable.bookmark.BookmarkPage
import com.hinnka.tsbrowser.ui.composable.bookmark.EditBookmark
import com.hinnka.tsbrowser.ui.composable.download.DownloadPage
import com.hinnka.tsbrowser.ui.composable.history.HistoryPage
import com.hinnka.tsbrowser.ui.composable.main.MainPage
import com.hinnka.tsbrowser.ui.composable.wiget.PageContainer
import com.hinnka.tsbrowser.ui.theme.TSBrowserTheme
import com.hinnka.tsbrowser.viewmodel.HomeViewModel
import com.hinnka.tsbrowser.viewmodel.LocalViewModel
import kotlinx.coroutines.launch

open class MainActivity : BaseActivity() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Initialize {
                TSBrowserTheme {
                    PageContainer("main") {
                        page("main") { MainPage() }
                        page("downloads") { DownloadPage() }
                        page("bookmarks") { BookmarkPage() }
                        page("history") { HistoryPage() }
                        page("addFolder") { AddFolder(it?.get(0) as Bookmark) }
                        page("editBookmark") { EditBookmark(it?.get(0) as Bookmark) }
                    }
                    ImeListener()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            TabManager.loadTabs(this@MainActivity)
        }

        handleIntent(intent)

        if (BuildConfig.DEBUG) {
            window.decorView.keepScreenOn = true
        }
    }

    @Composable
    fun Initialize(content: @Composable () -> Unit) {
        val initialized = remember {
            mutableStateOf(false)
        }
        Bookmark.init { initialized.value = true }
        if (initialized.value) {
            Providers(content)
        }
    }

    @Composable
    fun Providers(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalViewModel provides viewModel,
            content = content)
    }

    @Composable
    fun ImeListener() {
        val viewModel = LocalViewModel.current
        val scope = rememberCoroutineScope()

        val view = LocalView.current
        view.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight =
                WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(
                    WindowInsetsCompat.Type.ime())
            scope.launch {
                viewModel.imeHeightState.animateTo(imeHeight.bottom.toFloat())
            }
            insets
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        intent ?: return
        logD("handleIntent: ${intent.action} ${intent.extras?.keySet()?.joinToString(",")} ${intent.data}")
        when (intent.action) {
            Intent.ACTION_WEB_SEARCH -> handleSearch(intent)
            Intent.ACTION_VIEW -> handleOpen(intent)
        }
    }

    fun handleSearch(intent: Intent) {
        val query = intent.getStringExtra(SearchManager.QUERY) ?: ""
        val appId = intent.getStringExtra(Browser.EXTRA_APPLICATION_ID)
        val newSearch = intent.getBooleanExtra(SearchManager.EXTRA_NEW_SEARCH, false)

        logD("handleSearch: $query $appId $newSearch")
        if (appId != packageName || newSearch) {
            TabManager.newTab(this).apply {
                loadUrl(query.toUrl())
                active()
            }
        } else {
            TabManager.currentTab.value?.loadUrl(query.toUrl())
        }
    }

    fun handleOpen(intent: Intent) {
        val appId = intent.getStringExtra(Browser.EXTRA_APPLICATION_ID)
        val url = intent.data?.toString() ?: return

        logD("handleOpen: $appId")
        if (appId != packageName) {
            TabManager.newTab(this).apply {
                loadUrl(url)
                active()
            }
        } else {
            TabManager.currentTab.value?.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        TabManager.onResume(viewModel.uiState.value)
    }

    override fun onPause() {
        super.onPause()
        TabManager.onPause()
    }
}