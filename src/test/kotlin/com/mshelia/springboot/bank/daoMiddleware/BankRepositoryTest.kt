package com.mshelia.springboot.bank.daoMiddleware

import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.operationResult.OperationResult
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class BankRepositoryTest {

    @MockkBean(relaxed = true)
    private lateinit var dataSource: BankRepository

    @Test
    fun retrieveBanks() {
        every { dataSource.retrieveBanks() } returns OperationResult.Success(listOf())

        dataSource.retrieveBanks()

        verify(exactly = 1) { dataSource.retrieveBanks() }
    }

    @Test
    fun retrieveBank() {
        val accountId = "valid_accountId"

        every { dataSource.retrieveBank(accountId) } returns OperationResult.Success(
            Bank(
                accountNumber = "123345",
                trust = 12.9,
                transactionFee = 2
            )
        )

        dataSource.retrieveBank(accountId)

        verify(exactly = 1) { dataSource.retrieveBank(accountId) }
    }

    @Test
    fun addBank() {
        val bank = Bank(accountNumber = "123345", trust = 12.9, transactionFee = 2)

        every { dataSource.addBank(bank) } returns OperationResult.Success(bank)

        dataSource.addBank(bank)

        verify(exactly = 1) { dataSource.addBank(any()) }
    }

    @Test
    fun updateBank() {
        val bank = Bank(accountNumber = "123345", trust = 12.9, transactionFee = 2)

        every { dataSource.updateBank(bank) } returns OperationResult.Success(bank)

        dataSource.updateBank(bank)

        verify(exactly = 1) { dataSource.updateBank(any()) }
    }

    @Test
    fun deleteBank() {
        val accountId = "valid_accountId"

        every { dataSource.deleteBank(accountId) } returns OperationResult.Success(
            Bank(
                accountNumber = "123345",
                trust = 12.9,
                transactionFee = 2
            )
        )

        dataSource.deleteBank(accountId)

        verify(exactly = 1) { dataSource.deleteBank(any()) }
    }
}