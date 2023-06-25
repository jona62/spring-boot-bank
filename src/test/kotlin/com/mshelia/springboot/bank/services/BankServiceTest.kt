package com.mshelia.springboot.bank.services

import com.mshelia.springboot.bank.daoMiddleware.DaoMiddleware
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BankServiceTest {
    private val dataSource: DaoMiddleware = mockk(relaxed = true)
    private val bankService = BankService(dataSource)

    @Test
    fun `should call its data source to retrieve banks`() {
        // act
        bankService.getBanks()

        // verify
        verify(exactly = 1) { dataSource.retrieveBanks() }
    }
}