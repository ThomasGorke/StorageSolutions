package com.thomasgorke.storagesolution.add_author

import com.thomasgorke.storagesolution.core.StorageType
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val addAuthorModule = module {
    fragment { AddAuthorView() }
    viewModel { (storageType: StorageType) ->
        AddAuthorViewModel(
            storageType = storageType,
            dataRepo = get()
        )
    }
}