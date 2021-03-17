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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.thomasgorke.storagesolution.base.ui.viewBinding
import com.thomasgorke.storagesolution.databinding.ActivityMainBinding
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)

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
        binding.bnvMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.add_author -> binding.bnvMain.visibility = View.GONE
                else -> binding.bnvMain.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    override fun onBackPressed() {
        if (!navController.popBackStack()) super.onBackPressed()
    }
}
