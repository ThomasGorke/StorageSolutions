package com.thomasgorke.storagesolution.sqldatabase

import com.thomasgorke.storagesolution.core.StorageType
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val sqlModule = module {
    fragment { SqlView() }
    viewModel { (storageType: StorageType) ->
        SqlViewModel(
            dataRepo = get(),
            storageType = storageType
        )
    }
}