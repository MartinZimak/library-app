package cz.zimi.library.loan

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/loans")
class LoanController(
    private val service: LoanService,
) {
    @GetMapping("/{id}")
    fun getLoan(@PathVariable id: Long): LoanResponse =
        service.getById(id = id).toResponse()

    @GetMapping
    fun listAll(): List<LoanResponse> =
        service.getAll().map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun borrow(@Valid @RequestBody request: BorrowRequest): LoanResponse =
        service.borrow(bookId = request.bookId, userId = request.userId).toResponse()

    @PostMapping("/{id}/return")
    fun returnLoan(@PathVariable id: Long): LoanResponse =
        service.returnLoan(loanId = id).toResponse()
}