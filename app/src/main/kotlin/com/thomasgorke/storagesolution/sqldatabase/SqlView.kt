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

package com.thomasgorke.storagesolution.sqldatabase

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.florianschuster.control.bind
import at.florianschuster.control.distinctMap
import com.thomasgorke.storagesolution.AuthorAdapter
import com.thomasgorke.storagesolution.R
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.databinding.FragmentDatabaseBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.lifecycle.events

class SqlView : Fragment(R.layout.fragment_database) {

    private val args: SqlViewArgs by navArgs()

    private val binding by viewBinding(FragmentDatabaseBinding::bind)
    private val navController by lazy(::findNavController)
    private val viewModel by viewModel<SqlViewModel>() {
        parametersOf(args.storageType)
    }

    private val authorAdapter by inject<AuthorAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDbData.adapter = authorAdapter

        registerActions()
        registerStateListener()
    }

    private fun registerActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fabAddAuthor.clicks()
                .bind { navController.navigate(SqlViewDirections.sqlDatabaseToAddAuthor(StorageType.SQL)) }
                .launchIn(this)

            authorAdapter.interaction
                .bind {
                    navController.navigate(
                        SqlViewDirections.sqlDatabaseToAddNews(
                            StorageType.SQL,
                            it
                        )
                    )
                }
                .launchIn(this)


            viewLifecycleOwner.lifecycle.events()
                .filter { it == Lifecycle.Event.ON_RESUME }
                .map { SqlViewModel.Action.OnResume }
                .bind(viewModel::dispatch)
                .launchIn(this)
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.distinctMap(SqlViewModel.State::authors)
                .bind { authors ->
                    authorAdapter.submitList(authors)
                }
                .launchIn(this)
        }
    }

}
