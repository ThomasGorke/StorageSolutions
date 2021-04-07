package com.thomasgorke.storagesolution.add_news

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.local.sql.SqlDatabase
import com.thomasgorke.storagesolution.core.model.News
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class AddNewsViewModel(
    private val news: News?,
    private val authorId: String,
    private val dataRepo: DataRepo,
    private val storageType: StorageType,
    private val operationType: OperationType
) : ControllerViewModel<AddNewsViewModel.Action, AddNewsViewModel.State>() {

    sealed class Action {
        data class Add(val title: String, val content: String) : Action()
        data class Update(val title: String, val content: String, val id: String) : Action()
        object DeleteNews : Action()
    }

    sealed class Mutation {
        data class SetUiSetup(val data: UiSetup) : Mutation()
    }

    data class State(
        val title: String = "",
        val content: String = "",
        val uiSetup: UiSetup = UiSetup.Create()
    )

    sealed class Effect {
        object Success : Effect()
        object NewsDeleted : Effect()
    }

    override val controller =
        viewModelScope.createEffectController<Action, Mutation, State, Effect>(
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.Add -> flow {
                        dataRepo.insertNews(
                            storageType,
                            News(action.title, action.content),
                            authorId
                        )
                        emitEffect(Effect.Success)
                    }
                    is Action.Update -> flow {
                        dataRepo.updateNews(
                            storageType,
                            News(action.title, action.content, action.id)
                        )
                        emitEffect(Effect.Success)
                    }
                    is Action.DeleteNews -> flow {
                        dataRepo.deleteNews(storageType, "")
                        emitEffect(Effect.NewsDeleted)
                    }
                }
            },
            mutationsTransformer = { mutations ->
                mutations.onStart {
                    if (operationType == OperationType.CREATE) {
                        emit(Mutation.SetUiSetup(UiSetup.Create()))
                    } else {
                        emit(Mutation.SetUiSetup(UiSetup.Update()))
                    }
                }
            },
            reducer = { mutation, previousState ->
                when(mutation) {
                    is Mutation.SetUiSetup -> previousState.copy(uiSetup = mutation.data)
                }
            }
        )

    interface UiSetup {
        val toolbarTitle: Int
        val btnText: Int
        val showMenu: Boolean

        data class Create(
            override val toolbarTitle: Int = R.string.toolbar_title_add,
            override val btnText: Int = R.string.add,
            override val showMenu: Boolean = false
        ): UiSetup

        data class Update(
            override val toolbarTitle: Int = R.string.toolbar_title_update,
            override val btnText: Int = R.string.update,
            override val showMenu: Boolean = true
        ): UiSetup
    }
}
