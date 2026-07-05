package cz.zimi.library.loan

import cz.zimi.library.book.BookResponse
import cz.zimi.library.book.toResponse
import cz.zimi.library.user.UserResponse
import cz.zimi.library.user.toResponse
import java.time.LocalDate

data class BorrowRequest(
    val bookId: Long,
    val userId: Long
)

data class LoanResponse(
    val id: Long,
    val user: UserResponse,
    val book: BookResponse,
    val borrowedAt: LocalDate,
    val dueAt: LocalDate,
    val returnedAt: LocalDate?,
)

fun Loan.toResponse(): LoanResponse {
    return LoanResponse(
        id = id ?: error("Loan id is null"),
        user = user.toResponse(),
        book = book.toResponse(),
        borrowedAt = borrowedAt,
        dueAt = dueAt,
        returnedAt = returnedAt
    )
}
