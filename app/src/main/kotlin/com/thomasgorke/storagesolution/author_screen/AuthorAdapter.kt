package com.thomasgorke.storagesolution.author_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.databinding.ItemAuthorImageBinding
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class AuthorAdapter :
    ListAdapter<Author, AuthorViewHolder>(object : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean =
            oldItem == newItem
    }) {

    private val _interaction = BroadcastChannel<Long>(Channel.BUFFERED)
    val interaction: Flow<Long> = _interaction.asFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder =
        AuthorViewHolder(
            ItemAuthorImageBinding.inflate(
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
    private val binding: ItemAuthorImageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(author: Author, addNewsClick: (authorId: Long) -> Unit) {
        binding.tvAuthorName.text = author.name
        binding.ivAuthor.load(author.image)

        binding.root.setOnClickListener { addNewsClick(author.id) }
    }
}