package com.mshelia.springboot.bank.exceptions

import org.springframework.http.HttpStatus

data class CreateOperationException(override val message: String?, val status: HttpStatus) : RuntimeException(message)

data class GetOperationException(override val message: String?, val status: HttpStatus) : RuntimeException(message)

data class DeleteOperationException(override val message: String?, val status: HttpStatus) : RuntimeException(message)

data class UpdateOperationException(override val message: String?, val status: HttpStatus) : RuntimeException(message)