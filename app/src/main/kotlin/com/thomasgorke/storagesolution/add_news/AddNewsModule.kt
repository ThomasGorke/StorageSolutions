package com.thomasgorke.storagesolution.add_news

import com.thomasgorke.storagesolution.core.StorageType
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val addNewsModule = module {
    fragment { AddNewsView() }
    viewModel { (authorId: Long, storageType: StorageType) ->
        AddNewsViewModel(
            dataRepo = get(),
            sqlDatabase = get(),
            authorId = authorId,
            storageType = storageType
        )
    }
}