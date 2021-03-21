package com.thomasgorke.storagesolution.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.florianschuster.control.bind
import at.florianschuster.control.distinctMap
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.FragmentNewsBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.lifecycle.events

class NewsView : Fragment(R.layout.fragment_news) {

    private val args: NewsViewArgs by navArgs()

    private val binding by viewBinding(FragmentNewsBinding::bind)
    private val navController by lazy(::findNavController)
    private val viewModel by viewModel<NewsViewModel>() {
        parametersOf(args.authorId, args.storageType)
    }

    private val newsAdapter by inject<NewsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNews.adapter = newsAdapter

        registerActions()
        registerStateListener()
    }

    private fun registerActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.events()
                .filter { it == Lifecycle.Event.ON_RESUME }
                .map { NewsViewModel.Action.OnResume }
                .bind(viewModel::dispatch)
                .launchIn(this)

            binding.fabAddNews.clicks()
                .bind {
                    navController.navigate(
                        NewsViewDirections.newsToAddNews(
                            args.storageType,
                            args.authorId
                        )
                    )
                }
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