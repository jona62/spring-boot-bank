package com.mshelia.springboot.bank.daoMiddleware

import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.operationResult.OperationResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class BankRepositoryImpl @Autowired constructor(val jdbcTemplate: JdbcTemplate) : BankRepository {
    companion object {
        fun mapRow(resultSet: ResultSet, rowNum: Int): Bank {
            return Bank(
                resultSet.getString("accountNumber"),
                resultSet.getDouble("trust"),
                resultSet.getInt("transactionFee")
            )
        }
    }

    override fun retrieveBanks(): Collection<Bank> {
        return jdbcTemplate.query("SELECT * FROM Bank", Companion::mapRow)
    }

    override fun retrieveBank(accountNumber: String): OperationResult<Bank> {
        val bank = getBank(accountNumber)
            ?: return OperationResult.Failure("Could not find bank with account number $accountNumber", HttpStatus.NOT_FOUND)
        return OperationResult.Success(bank)
    }

    override fun addBank(bank: Bank): OperationResult<Bank> {
        return try {
            jdbcTemplate.update(
                "INSERT INTO Bank (accountNumber, trust, transactionFee) VALUES (?, ?, ?)",
                bank.accountNumber, bank.trust, bank.transactionFee)
            OperationResult.Success(bank)
        } catch (e: DuplicateKeyException) {
            OperationResult.Failure("Bank with account number ${bank.accountNumber} already exists", HttpStatus.BAD_REQUEST)
        } catch (e: DataAccessException) {
            OperationResult.Failure("Failed to add bank: ${e.message}")
        }
    }

    override fun updateBank(bank: Bank): OperationResult<Bank> {
        return try {
            jdbcTemplate.update(
                "UPDATE Bank SET accountnumber=?, trust=?, transactionFee=? WHERE accountnumber=?",
                bank.accountNumber,
                bank.trust,
                bank.transactionFee,
                bank.accountNumber
            )
            OperationResult.Success(bank)
        } catch (e: DataAccessException) {
            return OperationResult.Failure(e.message)
        }
    }

    override fun deleteBank(accountNumber: String): OperationResult<Bank> {
        val bank = getBank(accountNumber = accountNumber)
            ?: return OperationResult.Failure("Could not find bank with account number $accountNumber", HttpStatus.NOT_FOUND)

        return try {
            jdbcTemplate.update("DELETE FROM Bank WHERE accountnumber=?", accountNumber)
            OperationResult.Success(bank)
        } catch (e: DataAccessException) {
            return OperationResult.Failure("Failure During Update")
        }
    }

    private fun getBank(accountNumber: String): Bank? {
        return jdbcTemplate.query(
                "SELECT * FROM Bank WHERE accountNumber=?",
            Companion::mapRow,
                accountNumber
            ).firstOrNull()
    }
}