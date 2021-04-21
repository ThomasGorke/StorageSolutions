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

package com.thomasgorke.storagesolution

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.ActivityMainBinding
import com.thomasgorke.storagesolution.utils.Snacker
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    private val snacker: Snacker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        snacker.defineView(binding.root)

        binding.toolbar.setupWithNavController(
            navController,
            AppBarConfiguration(
                setOf(
                    R.id.shared_preferences,
                    R.id.file_storage,
                    R.id.sql_database,
                    R.id.room_database,
                    R.id.firebase_storage,
                )
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.shared_preferences,
                R.id.file_storage,
                R.id.sql_database,
                R.id.room_database,
                R.id.firebase_storage -> binding.bnvMain.visibility = View.VISIBLE
                else -> binding.bnvMain.visibility = View.GONE
            }

            if (destination.id != R.id.news) {
                supportActionBar?.title = destination.label
            }
        }

        binding.bnvMain.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    override fun onBackPressed() {
        if (!navController.popBackStack()) super.onBackPressed()
    }
}
