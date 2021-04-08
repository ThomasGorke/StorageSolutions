package com.thomasgorke.storagesolution.add_news

import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
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
        object DeleteNews : Action()
    }

    sealed class Mutation {
        data class SetUiSetup(val data: UiSetup) : Mutation()
        data class SetNewsData(val title: String, val content: String) : Mutation()
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
                        if (operationType == OperationType.CREATE) {
                            dataRepo.insertNews(
                                storageType,
                                News(action.title, action.content),
                                authorId
                            )
                        } else {
                            news?.let {
                                dataRepo.updateNews(
                                    storageType,
                                    News(action.title, action.content, it.id)
                                )
                            }
                        }
                        emitEffect(Effect.Success)
                    }
                    is Action.DeleteNews -> flow {
                        news?.let { dataRepo.deleteNews(storageType, it.id) }
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

                    news?.let { emit(Mutation.SetNewsData(it.headline, it.content)) }
                }
            },
            reducer = { mutation, previousState ->
                when (mutation) {
                    is Mutation.SetUiSetup -> previousState.copy(uiSetup = mutation.data)
                    is Mutation.SetNewsData -> previousState.copy(
                        title = mutation.title,
                        content = mutation.content
                    )
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
        ) : UiSetup

        data class Update(
            override val toolbarTitle: Int = R.string.toolbar_title_update,
            override val btnText: Int = R.string.update,
            override val showMenu: Boolean = true
        ) : UiSetup
    }
}
