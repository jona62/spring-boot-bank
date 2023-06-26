package com.mshelia.springboot.bank.daoMiddleware

import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.operationResult.OperationResult
import com.mshelia.springboot.bank.services.BankService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension

@Component
@ExtendWith(SpringExtension::class)
internal class BankRepositoryTest {

    @TestConfiguration
    class BankRepositoryConfiguration {
        @Bean
        fun bankService(dataSource: BankRepository) = BankService(dataSource)
    }

    @MockkBean(relaxed = true)
    private lateinit var dataSource: BankRepository

    @Autowired
    private lateinit var bankService: BankService

    @Test
    fun retrieveBanks() {
        every { dataSource.retrieveBanks() } returns listOf()

        bankService.getBanks()

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

        bankService.getBank(accountId)

        verify(exactly = 1) { dataSource.retrieveBank(accountId) }
    }

    @Test
    fun addBank() {
        val bank = Bank(accountNumber = "123345", trust = 12.9, transactionFee = 2)

        every { dataSource.addBank(bank) } returns OperationResult.Success(bank)

        bankService.addBank(bank)

        verify(exactly = 1) { dataSource.addBank(any()) }
    }

    @Test
    fun updateBank() {
        val bank = Bank(accountNumber = "123345", trust = 12.9, transactionFee = 2)

        every { dataSource.updateBank(bank) } returns OperationResult.Success(bank)

        bankService.updateBank(bank)

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

        bankService.deleteBank(accountId)

        verify(exactly = 1) { dataSource.deleteBank(any()) }
    }
}