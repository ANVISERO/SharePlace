package com.anvisero.shareplace.exception

import org.springframework.http.HttpStatus

class NotFoundException(resource: String, id: Any) : AppException(
    message = "$resource с ID = $id не найден",
    status = HttpStatus.NOT_FOUND
)

class UnauthorizedException : AppException(
    message = "Вы не авторизованы для выполнения этого действия",
    status = HttpStatus.UNAUTHORIZED
)

class ForbiddenException : AppException(
    message = "Доступ запрещен",
    status = HttpStatus.FORBIDDEN
)

class BadRequestException(message: String) : AppException(
    message = message,
    status = HttpStatus.BAD_REQUEST
)