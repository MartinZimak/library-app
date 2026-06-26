package cz.zimi.library.loan

import org.springframework.data.jpa.repository.JpaRepository

interface LoanRepository: JpaRepository<Loan, Long> {
    fun getByIdOrThrow(id: Long): Loan = findById(id).orElseThrow { LoanNotFoundException(id) }
}