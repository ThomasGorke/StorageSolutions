package com.thomasgorke.storagesolution.news

import com.thomasgorke.storagesolution.core.StorageType
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val newsModule = module {
    fragment { NewsView() }
    viewModel { (authorId: String, storageType: StorageType) ->
        NewsViewModel(
            snacker = get(),
            dataRepo = get(),
            authorId = authorId,
            storageType = storageType,
        )
    }
}