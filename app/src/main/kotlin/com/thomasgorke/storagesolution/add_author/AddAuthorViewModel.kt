package com.thomasgorke.storagesolution.add_author

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.google.android.material.snackbar.Snackbar
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.utils.Snacker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class AddAuthorViewModel(
    private val snacker: Snacker,
    private val dataRepo: DataRepo,
    private val storageType: StorageType,
) : ControllerViewModel<AddAuthorViewModel.Action, AddAuthorViewModel.State>() {

    sealed class Action {
        data class Add(val authorName: String, val image: Bitmap?) : Action()
    }

    sealed class Mutation

    class State

    sealed class Effect {
        object EmptyFieldError : Effect()
        object Success : Effect()
    }

    override val controller =
        viewModelScope.createEffectController<Action, Mutation, State, Effect>(
            dispatcher = Dispatchers.IO,
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.Add -> flow {
                        if (action.image == null) {
                            snacker.showError(R.string.error_empty_input)
                            emitEffect(Effect.EmptyFieldError)
                            return@flow
                        }

                        kotlin.runCatching {
                            dataRepo.insertAuthor(
                                storageType,
                                Author(action.authorName, action.image)
                            )
                            emitEffect(Effect.Success)
                        }.getOrElse {
                            snacker.showError(R.string.default_error_msg)
                        }


                    }
                }
            }
        )
}
