package com.mshelia.springboot.bank.controller

import com.mshelia.springboot.bank.daoMiddleware.mock.MockBankDataSource
import com.mshelia.springboot.bank.exceptions.CreateOperationException
import com.mshelia.springboot.bank.exceptions.DeleteOperationException
import com.mshelia.springboot.bank.exceptions.GetOperationException
import com.mshelia.springboot.bank.exceptions.UpdateOperationException
import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.services.BankService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.*
import java.util.UUID

@WebMvcTest(BankController::class)
internal class BankControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    private val baseUrl = "/api/banks"

    private val banks = MockBankDataSource.retrieveAllBanks()

    @MockkBean
    private lateinit var bankService: BankService

    @Suppress("UNCHECKED_CAST")
    @Nested
    @DisplayName("GET /api/banks/")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetBanks {

        @Test
        fun `should return all banks`() {
            every { bankService.getBanks() } returns banks

            val getResponse = mockMvc.get(baseUrl)
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
                .andReturn()
                .response

            val response = bodyToObject(getResponse.contentAsString, Array<Bank>::class.java) as Array<Bank>
            response.toList().zip(banks) { expectedBank, actualBank ->
                assertThat(expectedBank).isEqualTo(actualBank)
            }
        }
    }

    @Nested
    @DisplayName("GET /api/banks/{accountNumber}")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetBank {

        @Test
        fun `should return the bank with the given account Number`() {
            // when/then
            every { bankService.getBank(any()) } returns banks.first()
            val accountNumber = "124234"

            mockMvc.get("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.trust") { value("90.0") }
                    jsonPath("$.transactionFee") { value("17") }
                }

        }

        @Test
        fun `should return NOT FOUND if the account number does not exist`() {
            // when/then
            val invalidAccountNumber = "does_not_exist"
            every { bankService.getBank(invalidAccountNumber) } throws GetOperationException(
                "Could not find bank with account number $invalidAccountNumber",
                HttpStatus.NOT_FOUND
            )

            val getResponse = mockMvc.get("$baseUrl/$invalidAccountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
                .andReturn()
                .response

            assertThat(getResponse.contentAsString).isEqualTo("Could not find bank with account number $invalidAccountNumber")

            verify(exactly = 1) { bankService.getBank(any()) }
        }
    }

    @Nested
    @DisplayName("POST /api/banks/")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PostBank {

        @Test
        fun `should add the new bank`() {
            // when
            val accountId = UUID.randomUUID().toString()
            val newBank = Bank(accountId, 31.256, 5)

            every { bankService.addBank(newBank) } returns newBank

            // then
            val postResponse = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(newBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(bodyToJson(newBank))
                    }
                }
                .andReturn()
                .response

            assertThat(bodyToObject(postResponse.contentAsString)).isEqualTo(newBank)

            verify(exactly = 1) { bankService.addBank(any()) }
        }

        @Test
        fun `should return BAD REQUEST if bank with given account number already exists`() {
            // when
            val existingBankAccount = Bank("124234", 90.0, 17)

            every { bankService.addBank(existingBankAccount) } throws
                    CreateOperationException(
                        "Bank with account number ${existingBankAccount.accountNumber} already exists",
                        HttpStatus.BAD_REQUEST
                    )

            val postResponse = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(existingBankAccount)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
                .andReturn()
                .response

            assertThat(postResponse.contentAsString).isEqualTo("Bank with account number ${existingBankAccount.accountNumber} already exists")

            verify(exactly = 1) { bankService.addBank(any()) }
        }

        @Test
        fun `should return BAD REQUEST if bank fails model validation`() {
            // when
            val invalidBankAccount = Bank("", 90.0, 17)

            val postResponse = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(invalidBankAccount)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
                .andReturn()
                .response

            assertThat(postResponse.contentAsString).isEqualTo("AccountNumber must not be empty")
            // Validation errors are caught early, before any calls to the bank service is made
            verify(exactly = 0) { bankService.addBank(any()) }
        }
    }

    @Nested
    @DisplayName("PATCH /api/banks")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PatchExistingBank {
        @Test
        fun `should update an existing bank`() {
            // when
            val bankToUpdate = Bank("124234", 90.0, 17)

            every { bankService.updateBank(bankToUpdate) } returns bankToUpdate

            val patchResponse = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(bankToUpdate)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        json(bodyToJson(bankToUpdate))
                    }
                }
                .andReturn()
                .response

            assertThat(bodyToObject(patchResponse.contentAsString)).isEqualTo(bankToUpdate)

            verify(exactly = 1) { bankService.updateBank(any()) }
        }

        @Test
        fun `should return NOT FOUND if account number does not exist`() {
            // when
            val invalidBank = Bank("00000", 3.23, 13)

            every { bankService.updateBank(invalidBank) } throws UpdateOperationException(
                "Could not find bank with account number ${invalidBank.accountNumber}",
                HttpStatus.NOT_FOUND
            )

            val patchResponse = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(invalidBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
                .andReturn()
                .response

            assertThat(patchResponse.contentAsString).isEqualTo("Could not find bank with account number ${invalidBank.accountNumber}")

            verify(exactly = 1) { bankService.updateBank(any()) }
        }

        @Test
        fun `should return BAD REQUEST if bank fails model validation`() {
            // when
            val invalidBankAccount = Bank("1345", -90.0, 17)

            val patchResponse = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = bodyToJson(invalidBankAccount)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
                .andReturn()
                .response

            assertThat(patchResponse.contentAsString).isEqualTo("Trust must be positive")

            verify(exactly = 0) { bankService.updateBank(any()) }
        }
    }

    @Nested
    @DirtiesContext
    @DisplayName("DELETE /api/banks/{accountNumber}")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class DeleteExistingBank {
        @Test
        fun `should delete an existing bank`() {
            // when
            val accountNumber = banks.first().accountNumber

            every { bankService.deleteBank(accountNumber) } returns banks.first()

            val deleteResponse = mockMvc.delete("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }
                .andReturn()
                .response

            assertThat(bodyToObject(deleteResponse.contentAsString)).isEqualTo(banks.first())

            verify(exactly = 1) { bankService.deleteBank(any()) }
        }

        @Test
        fun `should return NOT FOUND if account number does not exist`() {
            // when/then
            val invalidAccountNumber = "does_not_exist"

            every { bankService.deleteBank(invalidAccountNumber) } throws DeleteOperationException(
                "Could not find bank with account number $invalidAccountNumber",
                HttpStatus.NOT_FOUND
            )
            val deleteResponse = mockMvc.delete("$baseUrl/$invalidAccountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
                .andReturn()
                .response

            assertThat(deleteResponse.contentAsString).isEqualTo("Could not find bank with account number $invalidAccountNumber")
        }
    }

    private fun bodyToJson(bank: Bank): String =
        objectMapper.writeValueAsString(bank)

    private fun bodyToObject(json: String, type: Class<out Any> = Bank::class.java) =
        objectMapper.readValue(json, type)
}
