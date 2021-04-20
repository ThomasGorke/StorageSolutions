package com.thomasgorke.storagesolution.author_screen

import com.thomasgorke.storagesolution.core.StorageType
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val authorModule = module {
    fragment { AuthorView() }
    viewModel { (storageType: StorageType) ->
        AuthorViewModel(
            snacker = get(),
            dataRepo = get(),
            storageType = storageType,
        )
    }
}