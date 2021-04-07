package com.thomasgorke.storagesolution.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.databinding.ItemNewsBinding
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class NewsAdapter(
) : ListAdapter<News, NewsViewHolder>(object : DiffUtil.ItemCallback<News>() {
    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
        oldItem == newItem
}) {

    private val _interaction = BroadcastChannel<News>(Channel.BUFFERED)
    val interaction: Flow<News> = _interaction.asFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position)) { news ->
            _interaction.offer(news)
        }
    }
}


class NewsViewHolder(
    private val binding: ItemNewsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: News, callback: (news: News) -> Unit) {
        binding.tvNewsTitle.text = data.headline
        binding.tvNewsContent.text = data.content
        binding.root.setOnClickListener { callback(data) }
    }
}