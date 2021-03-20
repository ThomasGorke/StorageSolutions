package com.thomasgorke.storagesolution.news

import androidx.lifecycle.viewModelScope
import at.florianschuster.control.createController
import at.florianschuster.control.createEffectController
import com.thomasgorke.storagesolution.add_news.AddNewsViewModel
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.model.News
import kotlinx.coroutines.flow.flow

class NewsViewModel(
    private val authorId: Long,
    private val dataRepo: DataRepo,
    private val storageType: StorageType
) : ControllerViewModel<NewsViewModel.Action, NewsViewModel.State>() {

    sealed class Action {
        object OnResume : Action()
    }

    sealed class Mutation {
        data class SetNews(val news: List<News>) : Mutation()
    }

    data class State(
        val news: List<News> = emptyList()
    )

    override val controller =
        viewModelScope.createController<Action, Mutation, State>(
            initialState = State(),
            mutator = { action ->
                when (action) {
                    is Action.OnResume -> flow {
                        dataRepo.getAllNewsByAuthorId(storageType, authorId)
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