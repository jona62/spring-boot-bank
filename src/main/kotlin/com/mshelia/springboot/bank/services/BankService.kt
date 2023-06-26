package com.mshelia.springboot.bank.services

import com.mshelia.springboot.bank.daoMiddleware.BankRepository
import com.mshelia.springboot.bank.exceptions.CreateOperationException
import com.mshelia.springboot.bank.exceptions.DeleteOperationException
import com.mshelia.springboot.bank.exceptions.GetOperationException
import com.mshelia.springboot.bank.exceptions.UpdateOperationException
import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.operationResult.OperationResult
import org.springframework.stereotype.Service

@Service
class BankService(private val dataSource: BankRepository) {

    fun getBanks(): Collection<Bank> {
        return when(val getAllRequestResult = dataSource.retrieveBanks()) {
            is OperationResult.Success -> getAllRequestResult.result
            is OperationResult.Failure -> throw GetOperationException(getAllRequestResult.message, getAllRequestResult.status)
        }
    }

    fun getBank(accountNumber: String): Bank {
        return when (val getRequestResult = dataSource.retrieveBank(accountNumber)) {
            is OperationResult.Success -> getRequestResult.result
            is OperationResult.Failure -> throw GetOperationException(getRequestResult.message, getRequestResult.status)
        }
    }

    fun addBank(bank: Bank): Bank {
        return when (val addRequestResult = dataSource.addBank(bank)) {
            is OperationResult.Success -> addRequestResult.result
            is OperationResult.Failure -> throw CreateOperationException(addRequestResult.message, addRequestResult.status)
        }
    }

    fun updateBank(bank: Bank): Bank {
        return when (val updateRequestResult = dataSource.updateBank(bank)) {
            is OperationResult.Success -> updateRequestResult.result
            is OperationResult.Failure -> throw UpdateOperationException(updateRequestResult.message, updateRequestResult.status)
        }
    }

    fun deleteBank(accountNumber: String): Bank {
        return when (val deleteBankResult = dataSource.deleteBank(accountNumber)) {
            is OperationResult.Success -> deleteBankResult.result
            is OperationResult.Failure -> throw DeleteOperationException(deleteBankResult.message, deleteBankResult.status)
        }
    }
}