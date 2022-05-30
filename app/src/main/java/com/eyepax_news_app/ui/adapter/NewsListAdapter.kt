package com.eyepax_news_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eyepax_news_app.R
import com.eyepax_news_app.model.Article
import kotlinx.android.synthetic.main.adapter_news_list.view.*

class NewsListAdapter: RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    companion object {
        private const val TYPE_NEWS = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_COLLEAGUE = 2
        private const val TYPE_HEADER = 3
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_news_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_NEWS
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(imageView)
            authorTv.text = if (article.author.isNullOrEmpty()) {
                context.getString(R.string.author_null)
            } else {
                article.author
            }
            headerTv.text = article.title
            dateTv.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}