package com.anvisero.shareplace.exception.handler

import com.anvisero.shareplace.exception.AppException
import com.anvisero.shareplace.exception.model.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            error = ex.status.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity(error, ex.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }

        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = message,
            path = request.requestURI
        )

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFound(
        ex: UsernameNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message ?: "User not found",
            path = request.requestURI
        )

        return ResponseEntity(error, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            error = "Internal Server Error",
            message = ex.message ?: "Unexpected error occurred",
            path = request.requestURI
        )

        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}