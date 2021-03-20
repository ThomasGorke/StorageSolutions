/*
 * Copyright 2020 Tailored Media GmbH.
 * Created by Florian Schuster.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thomasgorke.storagesolution.author_screen

import androidx.lifecycle.viewModelScope
import at.florianschuster.control.Controller
import at.florianschuster.control.createController
import com.thomasgorke.storagesolution.base.ui.ControllerViewModel
import com.thomasgorke.storagesolution.core.DataRepo
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.model.Author
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class AuthorViewModel(
    private val dataRepo: DataRepo,
    private val storageType: StorageType
) : ControllerViewModel<AuthorViewModel.Action, AuthorViewModel.State>() {

    sealed class Action {
        object OnResume : Action()
    }

    sealed class Mutation {
        data class SetAuthors(val authors: List<Author>) : Mutation()
    }

    data class State(
        val authors: List<Author> = emptyList()
    )

    init {
        Timber.d("Storage Type: ${storageType}")
    }

    override val controller: Controller<Action, State> =
        viewModelScope.createController<Action, Mutation, State>(
            initialState = State(),
            mutator = { action ->
                when (action) {
                    Action.OnResume -> flow {
                        emit(Mutation.SetAuthors(dataRepo.getAllAuthors(storageType)))
                    }
                }
            },
            reducer = { mutation, previousState ->
                when (mutation) {
                    is Mutation.SetAuthors -> previousState.copy(authors = mutation.authors)
                }
            }
        )
}