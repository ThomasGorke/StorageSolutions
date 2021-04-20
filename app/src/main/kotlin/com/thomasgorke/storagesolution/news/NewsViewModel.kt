package com.thomasgorke.storagesolution.news

import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.utils.Snacker
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.model.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow

class NewsViewModel(
    private val authorId: String,
    private val snacker: Snacker,
    private val dataRepo: DataRepo,
    private val storageType: StorageType,
) : ControllerViewModel<NewsViewModel.Action, NewsViewModel.State>() {

    sealed class Action {
        object OnResume : Action()
        object OnDeleteAuthor : Action()
    }

    sealed class Mutation {
        data class SetNews(val news: List<News>) : Mutation()
    }

    data class State(
        val news: List<News> = emptyList()
    )

    sealed class Effect {
        object AuthorDeleted : Effect()
    }

    override val controller =
        viewModelScope.createEffectController<Action, Mutation, State, Effect>(
            dispatcher = Dispatchers.IO,
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.OnResume -> flow {
                        val news = kotlin.runCatching {
                            dataRepo.getAllNewsByAuthorId(storageType, authorId)
                        }.getOrElse {
                            snacker.showDefaultError()
                            emptyList()
                        }

                        emit(Mutation.SetNews(news))
                    }
                    is Action.OnDeleteAuthor -> flow {
                        dataRepo.deleteAuthor(storageType, authorId)
                        emitEffect(Effect.AuthorDeleted)
                    }
                }
            },
            reducer = { mutation, previousState ->
                when (mutation) {
                    is Mutation.SetNews -> previousState.copy(news = mutation.news)
                }
            }
        )
}