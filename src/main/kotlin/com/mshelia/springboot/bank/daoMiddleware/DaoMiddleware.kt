package com.mshelia.springboot.bank.daoMiddleware

import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.operationResult.OperationResult

interface DaoMiddleware {
    fun retrieveBanks(): Collection<Bank>

    fun retrieveBank(accountNumber: String): OperationResult<Bank>

    fun addBank(bank: Bank): OperationResult<Bank>

    fun updateBank(bank: Bank): OperationResult<Bank>

    fun deleteBank(accountNumber: String): OperationResult<Bank>
}