package com.thomasgorke.storagesolution.add_author

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.local.FileStorage
import com.thomasgorke.storagesolution.core.local.SpStorage
import com.thomasgorke.storagesolution.core.local.SqlDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.model.Author
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class AddAuthorViewModel(
    private val storageType: StorageType,
    private val dataRepo: DataRepo
) : ControllerViewModel<AddAuthorViewModel.Action, AddAuthorViewModel.State>() {

    sealed class Action {
        data class Add(val authorName: String, val image: Bitmap?) : Action()
    }

    sealed class Mutation

    class State

    sealed class Effect {
        object AnyError : Effect()
        object DatabaseError : Effect()
        object EmptyFieldError : Effect()
        object Success : Effect()
    }

    init {
        Timber.d("Storage Type: $storageType")
    }

    override val controller =
        viewModelScope.createEffectController<Action, Mutation, State, Effect>(
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.Add -> flow {
                        if (action.image == null) {
                            emitEffect(Effect.EmptyFieldError)
                            return@flow
                        }

                        dataRepo.insertAuthor(storageType, Author(action.authorName, action.image))

                        emitEffect(Effect.Success)
                    }
                }
            }
        )
}
