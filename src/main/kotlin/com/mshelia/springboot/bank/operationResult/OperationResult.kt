package com.mshelia.springboot.bank.operationResult

import org.springframework.http.HttpStatus

sealed class OperationResult<T> {
    /**
     * The result of a successful operation
     * @param T type of result returned from a success
     * @property result the valid value of the operation
     */
    data class Success<T>(val result: T): OperationResult<T>()

    /**
     * The result of a failed operation
     * @property message the message of a failure operation
     */
    data class Failure<T>(val message: String?, val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): OperationResult<T>()
}