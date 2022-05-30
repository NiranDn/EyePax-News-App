package com.eyepax_news_app.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eyepax_news_app.Globals
import com.eyepax_news_app.Globals.NEWS_DETAILS
import com.eyepax_news_app.Globals.SEARCH_NEWS
import com.eyepax_news_app.R
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentSearchNewsBinding
import com.eyepax_news_app.ui.DashBoardActivity
import com.eyepax_news_app.ui.adapter.CategoryAdapter
import com.eyepax_news_app.ui.adapter.LatestNewsAdapter
import com.eyepax_news_app.ui.adapter.NewsListAdapter
import com.eyepax_news_app.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.filter_bottomsheet.view.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.categoryRv
import kotlinx.android.synthetic.main.fragment_search_news.newsRv
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Search fragment.
 */
class SearchNewsFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchNewsBinding
    private val mViewModel by lazy { ViewModelProvider(requireActivity()).get(SearchNewsViewModel::class.java) }
    private lateinit var mCategoryAdapter: CategoryAdapter
    private lateinit var mNewsListAdapter: NewsListAdapter
    private lateinit var mLatestNewsAdapter: LatestNewsAdapter
    private val args: SearchNewsFragmentArgs by navArgs()

    private var mSelectedFiltersMap: HashMap<String, Int> = HashMap()
    private var mNewsFilter = ""
    private var mSelectedWord = "business"
    private val COUNTRY_CODE = "us"
    private var isScreenNavigation = ""
    private var mSearchedWord = ""
    private var isError = false
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isScreenNavigation = args.search
        initView(isScreenNavigation)
        observeViewModel(view)
    }

    private fun initView(newsSearchingType: String) {
        if (newsSearchingType.equals(SEARCH_NEWS, ignoreCase = true)) {
            // Search screen
            searchSection.visibility = View.VISIBLE
            initNewsListsWithSearch()
            initClickListenerForSearchScreen()
            searchListener()

            mViewModel.getCategories()
            mViewModel.filterNews(mSelectedWord, mNewsFilter)
        } else {
            // Latest news list
            searchSection.visibility = View.GONE
            initNewsList()
            initClickListenerForNewsScreen()

            mViewModel.getLatestNews(COUNTRY_CODE)
        }
    }

    private fun observeViewModel(view: View) {
        mViewModel.latestNews.observe(viewLifecycleOwner) { response ->
            val spinner = showLoading(requireContext())
            when (response) {
                is Resource.Success -> {
                    spinner.dismiss()
                    response.data?.let { latestNews ->
                        if (isScreenNavigation == NEWS_DETAILS) {
                            mLatestNewsAdapter.differ.submitList(latestNews.articles.toList())
                        }
                    }
                }
                is Resource.Error -> {
                    spinner.cancel()
                    response.message?.let { message ->
                        showAlert(message, view)
                    }
                }
                is Resource.Loading -> {
//                    spinner.show()
                }
            }
        }

        mViewModel.categories.observe(viewLifecycleOwner) { response ->
            mCategoryAdapter.categoryies.submitList(response.toList())
        }

        mViewModel.filteredNews.observe(viewLifecycleOwner) { response ->
            val spinner = showLoading(requireContext())
            when (response) {
                is Resource.Success -> {
                    spinner.cancel()
                    response.data?.let { latestNews ->
                        if (isScreenNavigation == SEARCH_NEWS) {
                            mNewsListAdapter.differ.submitList(latestNews.articles.toList())
                            showSearchResultCount(latestNews.articles.toList().size)
                        }
                    }
                }
                is Resource.Error -> {
                    spinner.cancel()
                    response.message?.let { message ->
                        showAlert(message, view)
                    }
                }
                is Resource.Loading -> {
//                    spinner.show()
                }
            }
        }
    }

    /**
     * Setup Ui for search screen
     */
    private fun initNewsListsWithSearch() {
        mCategoryAdapter = CategoryAdapter()
        mNewsListAdapter = NewsListAdapter()

        categoryRv.apply {
            adapter = mCategoryAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
        newsRv.apply {
            adapter = mNewsListAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun initClickListenerForSearchScreen() {
        mCategoryAdapter.setOnItemClickListener {
            mSelectedWord = it.categoryName
            mViewModel.isClearPreviousData = true
            mViewModel.filterNews(it.categoryName, mNewsFilter)
        }

        mNewsListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_newsDetailFragment,
                bundle
            )
        }

        filter.setOnClickListener {
            showBottomSheetForSort()
        }
    }

    /**
     * Setup Ui for latest news screen
     */
    private fun initNewsList() {
        mLatestNewsAdapter = LatestNewsAdapter()
        newsRv.apply {
            adapter = mLatestNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun initClickListenerForNewsScreen() {
        mLatestNewsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_newsDetailFragment,
                bundle
            )
        }

        filter.setOnClickListener {
            showBottomSheetForSort()
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Globals.PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                if (isScreenNavigation == SEARCH_NEWS) {
                    mViewModel.filterNews(mSelectedWord, mNewsFilter)
                } else {
                    mViewModel.getLatestNews(COUNTRY_CODE)
                }
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                (activity as DashBoardActivity).showAndHideNavigationView(false)
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                (activity as DashBoardActivity).showAndHideNavigationView(true)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSearchResultCount(count: Int) {
        foundCountTV.text = "${"About "}$count${" results for "}"
        searchKeyWordTV.text = mSelectedWord
    }

    /**
     * Search query listener
     */
    private fun searchListener() {
        searchViewEt.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) { searchNews(query) }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchNews(newText)
                return true
            }
        })
    }

    private fun searchNews(query: String) {
        searchJob?.cancel()
        searchJob = MainScope().launch {
            delay(600L)
            mViewModel.isClearPreviousData = true
            if (query.isNotEmpty()) {
                mViewModel.filterNews(query, mNewsFilter)
                mSearchedWord = query
            } else {
                mViewModel.filterNews(mSelectedWord, mNewsFilter)
                mSearchedWord = mSelectedWord
            }

        }
    }

    private fun showBottomSheetForSort() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        var isReset = false
        val parentView = layoutInflater.inflate(R.layout.filter_bottomsheet, null)
        bottomSheetDialog.setContentView(parentView)
        val checkedChipId = mSelectedFiltersMap[mNewsFilter] ?: 0
        if (checkedChipId != 0) parentView.findViewById<Chip>(
            mSelectedFiltersMap[mNewsFilter] ?: 0
        ).isChecked = true

        parentView.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (!isReset) {
                val chip = parentView.findViewById<Chip>(checkedId)
                if (chip.isChecked) {
                    mNewsFilter = chip.text.toString()
                    mSelectedFiltersMap.clear()
                    mSelectedFiltersMap[mNewsFilter] = checkedId
                }
            } else {
                isReset = false
            }
        }

        parentView.resetBtn.setOnClickListener {
            val chipId = mSelectedFiltersMap[mNewsFilter] ?: 0
            if (chipId != 0) {
                isReset = true
                parentView.findViewById<Chip>(mSelectedFiltersMap[mNewsFilter] ?: 0).isChecked = false
                mNewsFilter = ""
            }
        }

        parentView.saveBtn.setOnClickListener {
            if (mNewsFilter.isNotEmpty()) {
                mViewModel.isClearPreviousData = true
                mViewModel.filterNews(mSelectedWord, mNewsFilter)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }
}