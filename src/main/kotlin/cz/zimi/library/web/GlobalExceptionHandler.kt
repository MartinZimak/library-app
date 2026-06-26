package cz.zimi.library.web

import cz.zimi.library.book.BookNotFoundException
import cz.zimi.library.common.ConflictException
import cz.zimi.library.common.ResourceNotFoundException
import cz.zimi.library.user.EmailAlreadyExistsException
import cz.zimi.library.user.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(ex: ConflictException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val errors = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "neplatná hodnota") }

        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validace vstupu selhala")
        problem.setProperty("errors", errors)
        return problem
    }
}
