package com.mshelia.springboot.bank.controller

import com.mshelia.springboot.bank.model.Bank
import com.mshelia.springboot.bank.services.BankService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/banks")
class BankController(private val bankService: BankService) {

    @GetMapping
    fun getAllBanks(): Collection<Bank> = bankService.getBanks()

    @GetMapping("/{accountNumber}")
    fun getBank(@PathVariable accountNumber: String): Bank = bankService.getBank(accountNumber)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBank(@RequestBody @Valid bank: Bank): Bank = bankService.addBank(bank)

    @PatchMapping
    fun updateBank(@RequestBody @Valid bank: Bank): Bank = bankService.updateBank(bank)

    @DeleteMapping("/{accountNumber}")
    fun deleteBank(@PathVariable accountNumber: String): Bank = bankService.deleteBank(accountNumber)
}