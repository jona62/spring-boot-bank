package com.mshelia.springboot.bank.daoMiddleware.mock

import com.mshelia.springboot.bank.model.Bank

object MockBankDataSource {
    private val banks = mutableListOf(
        Bank("124234", 90.0, 17),
        Bank("023988", 12.0, 21),
        Bank("115411", 423.0, 100),
        Bank("100341", 423.0, 56)
    )

    fun retrieveAllBanks() = banks

}