package com.eyepax_news_app.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eyepax_news_app.Globals.NEWS_DETAILS
import com.eyepax_news_app.Globals.PAGE_SIZE
import com.eyepax_news_app.Globals.SEARCH_NEWS
import com.eyepax_news_app.R
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentDashboardBinding
import com.eyepax_news_app.ui.DashBoardActivity
import com.eyepax_news_app.ui.adapter.CategoryAdapter
import com.eyepax_news_app.ui.adapter.LatestNewsAdapter
import com.eyepax_news_app.ui.adapter.NewsListAdapter
import com.eyepax_news_app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * Dashboard fragment
 */
@AndroidEntryPoint
class DashboardFragment : BaseFragment() {
    private lateinit var binding: FragmentDashboardBinding
    private val mViewModel by lazy { ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java) }

    private var isApiRequest = false
    private lateinit var mLatestNewsAdapter: LatestNewsAdapter
    private lateinit var mCategoryAdapter: CategoryAdapter
    private lateinit var mNewsListAdapter: NewsListAdapter
    private var mSelectedCategory = "business"
    private var isError = false
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(view)
        initView()
        initClickListener()
        getLatestNews()
        getCategories()
        filterNewsByCategory()
    }

    private fun initView() {
        mLatestNewsAdapter = LatestNewsAdapter()
        mCategoryAdapter = CategoryAdapter()
        mNewsListAdapter = NewsListAdapter()

        headLinesRv.apply {
            adapter = mLatestNewsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        categoryRv.apply {
            adapter = mCategoryAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        newsRv.apply {
            adapter = mNewsListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            hasFixedSize()
            addOnScrollListener(scrollListener)
        }
    }

    private fun observeViewModel(view: View) {
        mViewModel.latestNews.observe(viewLifecycleOwner) { response ->
            val spinner = showLoading(requireContext())
            when (response) {
                is Resource.Success -> {
                    spinner.dismiss()
                    response.data?.let { latestNews ->
                        mLatestNewsAdapter.differ.submitList(latestNews.articles.toList())
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
                        mNewsListAdapter.differ.submitList(latestNews.articles.toList())
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

        initScrollListener()
    }

    private fun initClickListener() {
        mCategoryAdapter.setOnItemClickListener {
            mSelectedCategory = it.categoryName
            mViewModel.isClearPreviousData = true
            mViewModel.filterNewsByCategory(it.categoryName)
        }

        mLatestNewsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_dashboardFragment_to_newsDetailFragment,
                bundle
            )
        }

        mNewsListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_dashboardFragment_to_newsDetailFragment,
                bundle
            )
        }

        searchView.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("search", SEARCH_NEWS)
            }
            findNavController().navigate(
                R.id.action_dashboardFragment_to_searchNewsFragment,
                bundle
            )
        }

        viewAll.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("search", NEWS_DETAILS)
            }
            findNavController().navigate(
                R.id.action_dashboardFragment_to_searchNewsFragment,
                bundle
            )
        }
    }

    private fun initScrollListener() {
        scrollView.setOnScrollChangeListener(mScrollChangeListener)
    }

    private fun getLatestNews() {
        mViewModel.getLatestNews(countryCode = "us")
    }

    private fun getCategories() {
        mViewModel.getCategories()
    }

    private fun filterNewsByCategory() {
        mViewModel.filterNewsByCategory(category = mSelectedCategory)
    }

    override fun onPause() {
        super.onPause()
        (activity as DashBoardActivity).showAndHideNavigationView(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as DashBoardActivity).showAndHideNavigationView(true)
    }

    private val mScrollChangeListener =
        NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            when {
                v != null -> {
                    if (scrollY + v.measuredHeight == v.getChildAt(0).measuredHeight) {
                        if (!isApiRequest) {
                            isApiRequest = true
                            mViewModel.getNewsForNextPage(mCategoryAdapter.selectedCategory)
                        }
                    }
                }
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
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                mViewModel.filterNewsByCategory(mSelectedCategory)
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
}