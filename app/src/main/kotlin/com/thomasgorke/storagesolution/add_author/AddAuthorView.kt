package com.thomasgorke.storagesolution.add_author

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.florianschuster.control.bind
import at.florianschuster.control.distinctMap
import coil.load
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.FragmentAddAuthorBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges
import timber.log.Timber

class AddAuthorView : Fragment(R.layout.fragment_add_author) {

    private val args: AddAuthorViewArgs by navArgs()

    private val binding by viewBinding(FragmentAddAuthorBinding::bind)
    private val
            navController by lazy(::findNavController)
    private val viewModel by viewModel<AddAuthorViewModel>() {
        parametersOf(args.storageType)
    }

    private var img: Bitmap? = null

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result: Bitmap ->
            img = result
            binding.ivAuthor.load(result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.btnAdd) {
            alpha = 0.5f
            isEnabled = false
        }

        registerActions()
        registerStateListener()
    }

    private fun registerActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnAdd.clicks()
                .map { Timber.d("AddButtonClicked") }
                .map { AddAuthorViewModel.Action.Add(binding.etAuthorName.text.toString(), img) }
                .bind(viewModel::dispatch)
                .launchIn(this)

            binding.etAuthorName.textChanges()
                .map { binding.etAuthorName.text.toString().isNotBlank() }
                .bind {
                    with(binding.btnAdd) {
                        alpha = if (it) 1f else 0.5f
                        isEnabled = it
                    }
                }
                .launchIn(this)

            binding.ivAuthor.clicks()
                .bind { imageResult.launch(null) }
                .launchIn(this)
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.distinctMap(AddAuthorViewModel.State::javaClass)
                .bind { /* do nothing */ }
                .launchIn(this)


            viewModel.controller.effects.onEach { effect ->
                when (effect) {
                    AddAuthorViewModel.Effect.AnyError -> {
                    }

                    AddAuthorViewModel.Effect.DatabaseError -> {

                    }

                    AddAuthorViewModel.Effect.EmptyFieldError -> {
                        Timber.e("Empty field error")
                    }
                    AddAuthorViewModel.Effect.Success -> {
                        Timber.d("Navigate to prev screen")
                        navController.popBackStack()
                    }
                }
            }
                .launchIn(this)
        }
    }
}