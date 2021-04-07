package com.thomasgorke.storagesolution.add_news

import com.thomasgorke.storagesolution.core.StorageType
import com.thomasgorke.storagesolution.core.model.News
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val addNewsModule = module {
    fragment { AddNewsView() }
    viewModel { (authorId: String, storageType: StorageType, operationType: OperationType, news: News?) ->
        AddNewsViewModel(
            news = news,
            dataRepo = get(),
            authorId = authorId,
            storageType = storageType,
            operationType = operationType
        )
    }
}