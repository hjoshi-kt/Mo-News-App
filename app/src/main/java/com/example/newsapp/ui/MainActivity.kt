package com.example.newsapp.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.R
import com.example.newsapp.application.MyApplication
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.models.Articles
import com.example.newsapp.network.NewsApiRepository
import com.example.newsapp.network.NewsApiViewModel
import com.example.newsapp.network.NewsApiViewModelFactory
import com.example.newsapp.notifications.CustomPushMessageListener
import com.example.newsapp.ui.adapters.NewsAdapter
import com.example.newsapp.ui.fragments.ClickListener
import com.example.newsapp.ui.fragments.ExampleDialog
import com.example.newsapp.ui.fragments.FifthFragment
import com.example.newsapp.ui.fragments.FirstFragment
import com.example.newsapp.ui.fragments.FourthFragment
import com.example.newsapp.ui.fragments.SecondFragment
import com.example.newsapp.ui.fragments.ThirdFragment
import com.example.newsapp.util.InApp
import com.example.newsapp.util.Utils
import com.google.gson.Gson
import com.moengage.cards.core.MoECardHelper
import com.moengage.cards.core.listener.SyncCompleteListener
import com.moengage.cards.core.model.SyncCompleteData
import com.moengage.cards.ui.CardActivity
import com.moengage.cards.ui.CardFragment
import com.moengage.cards.ui.MoECardUiHelper
import com.moengage.cards.ui.listener.OnCardClickListener
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.model.AppStatus
import com.moengage.inapp.MoEInAppHelper
import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.listeners.OnClickActionListener
import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.ClickData
import com.moengage.inapp.model.InAppData
import com.moengage.inapp.model.SelfHandledCampaignData
import com.moengage.pushbase.MoEPushHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, NewsAdapter.OnItemClickListener,
    SelfHandledAvailableListener,
    InAppLifeCycleListener, OnClickActionListener, SyncCompleteListener, OnCardClickListener {

    private lateinit var binding: ActivityMainBinding
    private val repository = NewsApiRepository()
    private val viewModel: NewsApiViewModel by viewModels {
        NewsApiViewModelFactory(repository)
    }
    private var newsAdapter : NewsAdapter? = null
    private lateinit var articles : MutableList<Articles>
    private lateinit var searchView : SearchView
    private lateinit var dataStore: DataStore<Preferences>
    private var sortBy : Int = 0 // 0 for new to old and 1 for old to new
    private lateinit var filters : Array<String>
    private var menuItem: MenuItem? = null
    private var sortByAdapter :  ArrayAdapter<String>? = null
    private lateinit var sharedPreferences : SharedPreferences
    companion object {
        private const val NEW_TO_OLD = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        articles = mutableListOf()
        supportActionBar?.elevation = 0f
        dataStore = createDataStore(Utils.SORT_BY)
        filters = resources.getStringArray(R.array.filters)
        binding.dropdown.visibility = View.GONE
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.grey)))
        sharedPreferences = getSharedPreferences(Utils.MO_APP_VERSION_PREF_KEY, MODE_PRIVATE)
        MoEPushHelper.getInstance().requestPushPermission(this)
        MoECardHelper.setSyncCompleteListener(this)
        MoECardUiHelper.setClickListener(this)

//        ExampleDialog(supportFragmentManager, "hjebrfbher", this, object : ClickListener {
//            override fun OnDismissListener(context: Context) {
//                Log.d(Utils.NEWS_APP_LOG,"In App dismissed")
////                MoEInAppHelper.getInstance().selfHandledDismissed(context, data)
//            }
//
//            override fun OnClickListener(context: Context) {
//                Log.d(Utils.NEWS_APP_LOG,"In App clicked")
////                MoEInAppHelper.getInstance().selfHandledClicked(context, data)
//            }
//        })

//        MoEPushHelper.getInstance().registerMessageListener(CustomPushMessageListener())

        (application as MyApplication).onEventOccurred = { data ->
            // Handle event here
            Log.d(Utils.NEWS_APP_LOG, "Event occurred with data: $data")
            val inApp = Gson().fromJson(data.campaign.payload, InApp::class.java)
            if (inApp.isDialog) {
                openDialog(data, inApp)
            } else {
                Toast.makeText(this, "this is a toast", Toast.LENGTH_SHORT).show()
            }
            MoEInAppHelper.getInstance().selfHandledShown(this, data)
        }

        trackApplicationStatus()
        MoEInAppHelper.getInstance().getSelfHandledInApp(this, this)
//        replaceFragment(FirstFragment())
        replaceFragment(CardFragment())
        MoEInAppHelper.getInstance().showInApp(this)
        MoEInAppHelper.getInstance().showNudge(this)
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(this)
        MoEInAppHelper.getInstance().setClickActionListener(this)
        binding.button.setOnClickListener{
            val fragment = supportFragmentManager.fragments.last()
            when (fragment::class.java) {
                FirstFragment::class.java -> replaceFragment(SecondFragment())
                SecondFragment::class.java -> replaceFragment(ThirdFragment())
                ThirdFragment::class.java -> replaceFragment(FourthFragment())
                FourthFragment::class.java -> replaceFragment(FifthFragment())
                else -> replaceFragment(FirstFragment())
            }
        }

        lifecycleScope.launch {
            var value = readDataStore(Utils.SORT_BY) // coroutine scope to get the default selection of user from local db
            if (TextUtils.isEmpty(value)){
                setDataStore(Utils.SORT_BY, NEW_TO_OLD) // if the value is empty it means user is opening the app for the first time. Hence add a default selection to new to old in the db
                value = NEW_TO_OLD
            }
            sortBy = value!!.toInt()
            if (sortByAdapter == null){
                setupSortByAdapter()
            }
            viewModel.makeApiCall() // make API call
        }

        binding.autoCompleteText.layout
        binding.autoCompleteText.onItemClickListener = this

        /**
         * Observe the LiveData `apiResponse` in the ViewModel to handle updates in the News API data.
         * This observer is triggered when there is a change in the LiveData, and it responds by updating
         * the UI components accordingly.
         */

        viewModel.apiResponse.observe(this) {
            // Check if the response is not null
            it?.let { newsAPIResponse ->
                // Check if the articles in the response are not null
                newsAPIResponse.articles?.let { newArticles ->
                    // Initialize the NewsAdapter with the new list of articles
                    initializeNewsAdapter(newArticles)
                    // Make the search menu item visible
                    menuItem?.isVisible = true
                    // Hide the progress bar
                    binding.progress.visibility = View.GONE
                    // Show the sorting dropdown
                    binding.dropdown.visibility = View.VISIBLE
                    // If the adapter is not yet set up, do so
                    if (sortByAdapter == null){
                        setupSortByAdapter()
                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainerNews.id,fragment)
        transaction.commit()
    }

    private fun trackApplicationStatus() {
        val packageManager = packageManager
        val packageName = packageName

        val versionCode: Int = try {
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
        val lastAppVersion = sharedPreferences.getInt(Utils.MO_APP_VERSION_PREF_KEY,Utils.DEFAULT_SAVED_APP_VERSION)
        if (lastAppVersion == Utils.DEFAULT_SAVED_APP_VERSION) {
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.INSTALL)
        } else if (lastAppVersion > versionCode) {
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.UPDATE)
        }
        sharedPreferences.edit().putInt(Utils.MO_APP_VERSION_PREF_KEY, versionCode).apply()
    }

    /**
     * Writes data to DataStore with the specified key-value pair.
     *
     * @param key The key for storing data in DataStore.
     * @param value The value to be stored.
     */
    private suspend fun setDataStore(key : String, value : String){
        val datastoreKey = preferencesKey<String>(key)
        dataStore.edit {
            it[datastoreKey] = value
        }
    }

    /**
     * Reads data from DataStore with the specified key.
     *
     * @param key The key for retrieving data from DataStore.
     * @return The retrieved data or null if the key is not found.
     */

    private suspend fun readDataStore(key : String) : String? {
        val datastoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[datastoreKey]
    }

    /**
     * Initializes the NewsAdapter with the provided list of articles.
     *
     * @param newArticles The list of articles to be displayed.
     */

    private fun initializeNewsAdapter(newArticles: MutableList<Articles>) {
        if (sortBy == 0){
            articles = sortByDescending(newArticles)
        } else {
            articles = sortByAscending(newArticles)
        }
        newsAdapter = NewsAdapter(this, articles.toMutableList(), this)
        binding.recyclerView.adapter = newsAdapter
    }

    /**
     * Updates the NewsAdapter with a new list of articles.
     *
     * @param newArticles The new list of articles.
     */

    private fun updateNewsAdapter(newArticles: List<Articles>) {
        newsAdapter?.updateNewsArticles(newArticles)
        newsAdapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) {
            return false
        }

        menuInflater.inflate(R.menu.menu, menu)
        menuItem = menu.findItem(R.id.search_menu)
        menuItem?.isVisible = false
        searchView = menuItem?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var listOfArticles : List<Articles> = articles.toList()

                // filter articles by text changed

                if (!TextUtils.isEmpty(newText)){
                    listOfArticles = articles.filter { article ->
                        if (TextUtils.isEmpty(article.title)){
                            return false
                        }
                        article.title?.lowercase(Locale.ROOT)!!.contains(newText!!.lowercase(Locale.ROOT))
                    }
                }

                // If sort by is 0 search by descending order

                if (sortBy == 0){
                    updateNewsAdapter(sortByDescending(listOfArticles))
                } else {
                    updateNewsAdapter(sortByAscending(listOfArticles))
                }

                if (listOfArticles.isEmpty()){
                    manageLayouts(false)
                } else {
                    manageLayouts(true)
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Changes the visibility of layout after searching int he articles. If search results as null we have have to show no articles and hide recyclerview and dropdown view and vice-versa.
     *
     * @param show true to show recycler view and false to hide it
     */

    private fun manageLayouts(show : Boolean) {
        binding.recyclerView.visibility = if (show) View.VISIBLE else View.GONE
        binding.dropdown.visibility = if (show) View.VISIBLE else View.GONE
        binding.noArticles.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        sortBy = 1 - sortBy
        trackSortByStatus()
        setupSortByAdapter()
        lifecycleScope.launch {
            setDataStore(Utils.SORT_BY,sortBy.toString())
            if(sortBy == 0){
                updateNewsAdapter(sortByDescending(articles))
            } else {
                updateNewsAdapter(sortByAscending(articles))
            }
            binding.recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun trackSortByStatus() {
        val properties = Properties()
        properties
            .addAttribute("changed sort by", sortBy)
        MoEAnalyticsHelper.trackEvent(this, "sort by changed", properties)
    }

    /**
     * Sets up the adapter for the dropdown
     */

    private fun setupSortByAdapter() {
        binding.dropdown.editText?.setText(filters[sortBy])
        sortByAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, listOf(filters[1 - sortBy]))
        binding.autoCompleteText.setAdapter(sortByAdapter)
    }

    /**
     * Sorts the list of articles in ascending order based on the published date.
     *
     * @param listOfArticles The list of articles to be sorted.
     * @return The sorted list of articles.
     */

    private fun sortByAscending(listOfArticles : List<Articles>) : MutableList<Articles> {
        return listOfArticles.sortedBy {
            Utils.getMillisecondsFromDateAndTime(it.publishedAt)
        }.toMutableList()
    }

    /**
     * Sorts the list of articles in descending order based on the published date.
     *
     * @param listOfArticles The list of articles to be sorted.
     * @return The sorted list of articles.
     */

    private fun sortByDescending(listOfArticles : List<Articles>) : MutableList<Articles> {
        return listOfArticles.sortedByDescending {
            Utils.getMillisecondsFromDateAndTime(it.publishedAt)
        }.toMutableList()
    }

    /**
     * Manages the click listener for recycler view adapter. It picks up the url from article object and opens it in respective broswer
     *
     * @param position Position of the item clicked in the recycler view
     * @param listOfArticles The list of articles that are available in recyclerview
     */

    override fun onItemClick(position: Int, articles: List<Articles>) {
//        MoEInboxUiHelper.getInstance().setInboxAdapter(NotificationCenterAdapter())
//        val intent = Intent(this, InboxActivity::class.java)
//        startActivity(intent)
//        lifecycleScope.launch {
//            try {
//                withContext(Dispatchers.IO) {
//                    MoEInboxHelper.getInstance().fetchAllMessages(this@MainActivity)
//                }
                val intent = Intent(this, CardActivity::class.java)
                startActivity(intent)
//            } catch (e: Exception) {
//                // Handle the exception, for instance by showing an error message to the user
//            }
//        }

//        MoEInboxHelper.getInstance().fetchAllMessagesAsync(applicationContext) {
//            val intent = Intent(this@MainActivity, InboxActivity::class.java)
//                startActivity(intent)
//        }
    }

    override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
        Log.d(Utils.NEWS_APP_LOG,data.toString())
        if (data?.campaign != null){
            val inApp = Gson().fromJson(data.campaign.payload, InApp::class.java)
            openDialog(data, inApp)
            MoEInAppHelper.getInstance().selfHandledShown(this, data)
        }
    }

    private fun openDialog(data : SelfHandledCampaignData, inApp: InApp) {
        ExampleDialog(supportFragmentManager, inApp, this, object : ClickListener {
            override fun OnDismissListener(context: Context) {
                Log.d(Utils.NEWS_APP_LOG,"In App dismissed")
                MoEInAppHelper.getInstance().selfHandledDismissed(context, data)
            }

            override fun OnClickListener(context: Context) {
                Log.d(Utils.NEWS_APP_LOG,"In App clicked")
                MoEInAppHelper.getInstance().selfHandledClicked(context, data)
            }
        })
    }

    override fun onDismiss(inAppData: InAppData) {
        Log.d(Utils.NEWS_APP_LOG, "in app dismissed")
    }

    override fun onShown(inAppData: InAppData) {
        Log.d(Utils.NEWS_APP_LOG, "in app shown")
    }

    override fun onClick(clickData: ClickData): Boolean {
        Log.d(Utils.NEWS_APP_LOG, "in app clicked")
        return false
    }

    override fun onSyncComplete(data: SyncCompleteData?) {

        Log.d(Utils.NEWS_APP_LOG, "Sync completed for cards : " + data.toString())

        MoECardHelper.getNewCardCountAsync(this){
            Log.d(Utils.NEWS_APP_LOG, "New Card Count Data : " + it.toString())
        }

        MoECardHelper.getUnClickedCardCountAsync(this){
            Log.d(Utils.NEWS_APP_LOG, "Unclicked Card Count Data : " + it.toString())
        }
    }

    override fun onCardClick(clickData: com.moengage.cards.ui.model.ClickData): Boolean {
        Log.d(Utils.NEWS_APP_LOG, "Card clicked data : $clickData")
        return false
    }
}
