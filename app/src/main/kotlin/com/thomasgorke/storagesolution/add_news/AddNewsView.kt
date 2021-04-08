package com.thomasgorke.storagesolution.add_news

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.florianschuster.control.bind
import at.florianschuster.control.distinctMap
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.FragmentAddNewsBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks

class AddNewsView : Fragment(R.layout.fragment_add_news) {

    private val args: AddNewsViewArgs by navArgs()

    private val binding by viewBinding(FragmentAddNewsBinding::bind)
    private val navController by lazy(::findNavController)
    private val viewModel by viewModel<AddNewsViewModel> {
        parametersOf(args.authorId, args.storageType, args.operationType, args.news)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerActions()
        registerStateListener()
    }

    private fun registerActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnAdd.clicks()
                .map {
                    AddNewsViewModel.Action.Add(
                        binding.etHeadline.text.toString(),
                        binding.etContent.text.toString()
                    )
                }
                .bind(viewModel::dispatch)
                .launchIn(this)
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.distinctMap(AddNewsViewModel.State::title)
                .bind { binding.etHeadline.setText(it) }
                .launchIn(this)

            viewModel.state.distinctMap(AddNewsViewModel.State::content)
                .bind { binding.etContent.setText(it) }
                .launchIn(this)

            viewModel.state.distinctMap(AddNewsViewModel.State::uiSetup)
                .bind {
                    setMenuVisibility(it.showMenu)
                    requireActivity().setTitle(it.toolbarTitle)
                    binding.btnAdd.setText(it.btnText)
                }
                .launchIn(this)

            viewModel.controller.effects.onEach { effect ->
                when (effect) {
                    AddNewsViewModel.Effect.Success -> navController.navigateUp()
                    AddNewsViewModel.Effect.NewsDeleted -> navController.navigateUp()
                }
            }.launchIn(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_news, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete) {
            viewModel.dispatch(AddNewsViewModel.Action.DeleteNews)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}