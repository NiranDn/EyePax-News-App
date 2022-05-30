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
import kotlinx.android.synthetic.main.adapter_latest_new.view.*

class LatestNewsAdapter: RecyclerView.Adapter<LatestNewsAdapter.HeadlineViewHolder>() {
    private var onItemClickListener: ((Article) -> Unit)? = null

    inner class HeadlineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(article: Article, newArticle: Article): Boolean {
            return article.url == newArticle.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        return HeadlineViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_latest_new,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        val news = differ.currentList[position]
        holder.itemView.apply {
            authorTv.text = news.author
            headLineTv.text = news.title
            descriptionTV.text = news.description
            Glide
                .with(this)
                .load(news.urlToImage)
                .error(R.drawable.ic_loading)
                .into(imageView)

            setOnClickListener {
                onItemClickListener?.let { it(news) }
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}