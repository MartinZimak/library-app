package cz.zimi.library.loan

import cz.zimi.library.book.BookNotAvailableException
import cz.zimi.library.book.BookRepository
import cz.zimi.library.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LoanService(
    private val loanRepository: LoanRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {
    fun getAll(): List<Loan> = loanRepository.findAll()
    fun getById(id: Long): Loan = loanRepository.getByIdOrThrow(id)

    @Transactional
    fun borrow(bookId: Long, userId: Long): Loan {
        val book = bookRepository.getByIdOrThrow(bookId)
        if (!book.available) {
            throw BookNotAvailableException(bookId)
        }
        val user = userRepository.getByIdOrThrow(userId)

        book.available = false

        val loan = Loan(
            book = book,
            user = user,
            borrowedAt = LocalDate.now(),
            dueAt = LocalDate.now().plusDays(LOAN_DAYS)
        )

        loanRepository.save(loan)

        return loan
    }

    @Transactional
    fun returnLoan(loanId: Long): Loan {
        val loan = loanRepository.getByIdOrThrow(loanId)

        if (loan.returnedAt != null) throw LoanAlreadyReturnedException(loanId)

        loan.returnedAt = LocalDate.now()
        loan.book.available = true

        return loan
    }

    companion object {
        private const val LOAN_DAYS = 30L
    }
}