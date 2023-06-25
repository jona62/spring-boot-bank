package com.mshelia.springboot.bank.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
internal class GlobalExceptionHandler {

    @ExceptionHandler(value = [CreateOperationException::class, GetOperationException::class, DeleteOperationException::class, UpdateOperationException::class])
    fun handleExceptions(e: Exception): ResponseEntity<String> {
        return when (e) {
            is CreateOperationException -> ResponseEntity(e.message, e.status)
            is GetOperationException -> ResponseEntity(e.message, e.status)
            is DeleteOperationException -> ResponseEntity(e.message, e.status)
            is UpdateOperationException -> ResponseEntity(e.message, e.status)
            else -> ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<String> {
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "" }
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}
