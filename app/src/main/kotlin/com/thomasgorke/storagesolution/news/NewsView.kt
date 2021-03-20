package com.thomasgorke.storagesolution.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import at.florianschuster.control.bind
import at.florianschuster.control.distinctMap
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.FragmentNewsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks

class NewsView : Fragment(R.layout.fragment_news) {

    private val binding by viewBinding(FragmentNewsBinding::bind)
    private val navController by lazy(::findNavController)
    private val viewModel by viewModel<NewsViewModel>()

    private val newsAdapter by inject<NewsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerActions()
        registerStateListener()
    }

    private fun registerActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fabAddNews.clicks()
                .bind {  }
                .launchIn(this)
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.distinctMap(NewsViewModel.State::news)
                .bind { newsAdapter.submitList(it) }
                .launchIn(this)
        }
    }
}