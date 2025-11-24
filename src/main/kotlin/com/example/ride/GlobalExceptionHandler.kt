package com.example.ride

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(
        ex: BadRequestException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return handleError(HttpStatus.BAD_REQUEST, ex, request)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        ex: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return handleError(HttpStatus.NOT_FOUND, ex, request)
    }

    fun handleError(
        stat: HttpStatus,
        ex: Exception,
        request: HttpServletRequest,
        message: String? = ex.message,
        description: Map<String, String>? = null
    ): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            status = stat.value(),
            error = stat.reasonPhrase,
            message = message,
            path = request.requestURI,
            description = description
        )
        return ResponseEntity.status(stat).body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        // After fields fail validation, Spring throws this exception
        // and attaches a bindingResult (containing field errors - the fields which failed validation)
        val errors = ex.fieldErrors.associateBy(
            keySelector = {it.field},
            valueTransform = {it.defaultMessage  ?: "Invalid field"}
        )

       return handleError(HttpStatus.BAD_REQUEST, ex, request,
           message = "Validation failed", description = errors)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest
    ) = handleError(HttpStatus.BAD_REQUEST, ex, request)

    // When request provides invalid UUID
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST

        val body = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = "Field ${ex.propertyName}:${ex.value} not of required type ${ex.requiredType}",
            path = request.requestURI,
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> =
        handleError(HttpStatus.BAD_REQUEST, ex, request, "Invalid request body")


    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val body = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(body)
    }
}