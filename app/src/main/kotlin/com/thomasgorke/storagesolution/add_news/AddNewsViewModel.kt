package com.thomasgorke.storagesolution.add_news

import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.local.SqlDatabase
import com.thomasgorke.storagesolution.core.model.News
import kotlinx.coroutines.flow.flow

class AddNewsViewModel(
    private val authorId: Long,
    private val storageType: StorageType,
    private val dataRepo: DataRepo,
    private val sqlDatabase: SqlDatabase
) : ControllerViewModel<AddNewsViewModel.Action, AddNewsViewModel.State>() {

    sealed class Action {
        data class Add(val title: String, val content: String) : Action()
    }

    sealed class Mutation

    data class State(
        val test: String = ""
    )

    sealed class Effect {
        object AnyError : Effect()
        object DatabaseError : Effect()
        object EmptyFieldError : Effect()
        object Success : Effect()
    }

    override val controller =
        viewModelScope.createEffectController<Action, Mutation, State, Effect>(
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.Add -> flow {
                        when(storageType){
                            StorageType.SQL -> sqlDatabase.insertNews(News(action.title, action.content, authorId))
                            StorageType.ROOM -> sqlDatabase.insertNews(News(action.title, action.content, authorId))
                            StorageType.FIREBASE -> sqlDatabase.insertNews(News(action.title, action.content, authorId))
                        }
                        emitEffect(Effect.Success)
                    }
                }
            }
        )
}
