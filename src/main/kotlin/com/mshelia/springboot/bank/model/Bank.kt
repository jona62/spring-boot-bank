package com.mshelia.springboot.bank.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Entity
data class Bank(
    @field:NotBlank(message = "AccountNumber must not be empty")
    @Id
    val accountNumber: String,
    @field:Positive(message = "Trust must be positive")
    val trust: Double,
    @field:NotNull @field:Positive(message = "TransactionFee must be positive")
    val transactionFee: Int
)