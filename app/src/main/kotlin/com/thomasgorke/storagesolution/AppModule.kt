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

import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.thomasgorke.storagesolution.add_author.addAuthorModule
import com.thomasgorke.storagesolution.add_news.addNewsModule
import com.thomasgorke.storagesolution.author_screen.AuthorAdapter
import com.thomasgorke.storagesolution.author_screen.authorModule
import com.thomasgorke.storagesolution.core.model.AppBuildInfo
import com.thomasgorke.storagesolution.news.NewsAdapter
import com.thomasgorke.storagesolution.news.newsModule
import com.thomasgorke.storagesolution.utils.Snacker
import com.thomasgorke.storagesolution.utils.SnackerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val appModule = module {
    single { provideAppBuildInfo(context = androidContext()) }
    single<Snacker> { SnackerImpl(context = androidContext()) }
    factory { AuthorAdapter() }
    factory { NewsAdapter() }

    single { Firebase.storage.reference }
    single { Firebase.firestore }

}

private fun provideAppBuildInfo(context: Context): AppBuildInfo = AppBuildInfo(
    debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
    buildType = BuildConfig.BUILD_TYPE,
    flavor = BuildConfig.FLAVOR,
    versionCode = BuildConfig.VERSION_CODE,
    versionName = BuildConfig.VERSION_NAME,
    baseUrl = BuildConfig.BASE_URL
)

internal val appModules = listOf(
    appModule,
    addAuthorModule,
    addNewsModule,
    authorModule,
    newsModule
)