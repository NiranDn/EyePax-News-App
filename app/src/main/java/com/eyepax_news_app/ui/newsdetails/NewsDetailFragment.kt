package com.eyepax_news_app.ui.newsdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentNewsDetailBinding
import com.eyepax_news_app.model.Article
import kotlinx.android.synthetic.main.fragment_news_detail.*


class NewsDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentNewsDetailBinding
    val args: NewsDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = args.article
        initView(article)
    }

    private fun initView(article: Article) {
        datesTv.text = article.publishedAt
        detailsTv.text = article.content + "\n" +article.description
        newsHeaderTv.text = article.title
        publisherTv.text = article.author
        Glide.with(this)
            .load(article.urlToImage)
            .into(imageView)
    }
}