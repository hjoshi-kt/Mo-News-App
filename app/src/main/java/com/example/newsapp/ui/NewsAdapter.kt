package com.example.newsapp.ui

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.NewsSingleItemBinding
import com.example.newsapp.models.Articles
import com.example.newsapp.util.Utils

/**
 * RecyclerView Adapter for displaying a list of news articles in the News App.
 *
 * @property context The context in which the adapter is used.
 * @property newsList The list of news articles to be displayed.
 * @property listener The click listener for handling item clicks.
 */

class NewsAdapter(private val context: Context, private var newsList: MutableList<Articles>, private val listener: OnItemClickListener) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    /**
     * Creates and returns a new instance of [NewsViewHolder] when needed by the RecyclerView.
     *
     * @param parent The parent view group.
     * @param viewType The type of the new view.
     * @return A new [NewsViewHolder] instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding, listener, newsList)
    }

    /**
     * Binds the data at the specified position to the [NewsViewHolder].
     *
     * @param holder The [NewsViewHolder] to bind the data to.
     * @param position The position of the item in the dataset.
     */
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        if (!TextUtils.isEmpty(newsItem.publishedAt)){
            holder.binding.time.visibility = View.VISIBLE
            holder.binding.time.text = Utils.getFormattedDateAndTime(newsItem.publishedAt!!)
        } else {
            holder.binding.time.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(newsItem.title)){
            holder.binding.title.visibility = View.VISIBLE
            holder.binding.title.text = newsItem.title!!
        } else {
            holder.binding.title.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(newsItem.author)){
            holder.binding.author.visibility = View.VISIBLE
            holder.binding.author.text = "Author : ".plus(newsItem.author!!)
        } else {
            holder.binding.author.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(newsItem.description)){
            holder.binding.description.visibility = View.VISIBLE
            holder.binding.description.text = newsItem.description!!
        } else {
            holder.binding.description.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(newsItem.urlToImage)){
            holder.binding.newsImage.visibility = View.VISIBLE
            Glide.with(context).load(newsItem.urlToImage).placeholder(R.drawable.baseline_downloading_24).error(R.drawable.image_not_found).into(holder.binding.newsImage)
        } else {
            holder.binding.newsImage.visibility = View.GONE
        }
    }

    /**
     * Returns the total number of items in the dataset.
     *
     * @return The total number of items.
     */
    override fun getItemCount(): Int {
        return newsList.size
    }

    /**
     * Updates the list of news articles with a new list.
     *
     * @param articles The new list of news articles.
     */
    fun updateNewsArticles(articles: List<Articles>) {
        this.newsList.clear()
        this.newsList.addAll(articles)
    }

    /**
     * ViewHolder class for holding the view of a single news article.
     *
     * @param binding The data binding instance for the news item view.
     * @param listener The click listener for handling item clicks.
     * @param newsList The list of news articles.
     */
    class NewsViewHolder(val binding: NewsSingleItemBinding, private val listener: OnItemClickListener, private val newsList : MutableList<Articles>) : RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * Handles click events on the news item.
         *
         * @param v The clicked view.
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, newsList)
            }
        }

    }

    /**
     * Interface for handling item clicks in the RecyclerView.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int, articles: List<Articles>)
    }

}
