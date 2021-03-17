package com.thomasgorke.storagesolution

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.databinding.ItemAuthorBinding
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class AuthorAdapter :
    ListAdapter<Author, AuthorViewHolder>(object : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean =
            oldItem == newItem
    }) {

    private val _interaction = BroadcastChannel<Long>(Channel.BUFFERED)
    val interaction: Flow<Long> = _interaction.asFlow()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder =
        AuthorViewHolder(
            ItemAuthorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(getItem(position)) {
            _interaction.offer(it)
        }
    }
}


class AuthorViewHolder(
    private val binding: ItemAuthorBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var isCollapsed = true
    private val newsAdapter = NewsAdapter()

    fun bind(data: Author, addNewsClick: (authorId: Long) -> Unit) {
        binding.tvAuthor.text = data.name
        binding.ivAuthor.load(data.image)
        binding.rvNews.adapter = newsAdapter
        newsAdapter.submitList(data.news)

        binding.ivToggle.setOnClickListener {
            isCollapsed = isCollapsed.not()
            binding.ivToggle.load(if (isCollapsed) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            binding.rvNews.visibility = if (isCollapsed) View.GONE else View.VISIBLE
            binding.ivAddNews.visibility = if (isCollapsed) View.GONE else View.VISIBLE
        }

        binding.ivAddNews.setOnClickListener {
            addNewsClick(data.id)
        }
    }
}