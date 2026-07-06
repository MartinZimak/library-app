package cz.zimi.library.loan

import cz.zimi.library.book.Book
import cz.zimi.library.book.BookNotAvailableException
import cz.zimi.library.book.BookNotFoundException
import cz.zimi.library.book.BookRepository
import cz.zimi.library.user.User
import cz.zimi.library.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Čistý unit test LoanService — žádný Spring kontext, žádná DB.
 * Závislosti (repository) jsou MockK mocky, takže testujeme JEN logiku service
 * v izolaci. Nejrychlejší patro test pyramidy.
 */
class LoanServiceTest {

    // mockk<T>() vyrobí "strict" mock: každé volání, které si předem nevystubujeme
    // přes every { }, shodí test. To nás nutí přesně popsat, co service dělá.
    private val loanRepository = mockk<LoanRepository>()
    private val bookRepository = mockk<BookRepository>()
    private val userRepository = mockk<UserRepository>()

    // System Under Test skládáme ručně konstruktorem (to je celá "DI" v unit testu).
    private val service = LoanService(loanRepository, bookRepository, userRepository)

    // Tovární metody na testovací data (fixtures). Používáme REÁLNÉ entity —
    // mockujeme jen hranici (repository), ne doménové objekty.
    private fun sampleBook(available: Boolean = true, id: Long = 1L) =
        Book(title = "Hobit", author = "Tolkien", isbn = "9788025707418", available = available, id = id)

    private fun sampleUser(id: Long = 1L) =
        User(firstName = "Martin", lastName = "Zimak", email = "martin@example.com", id = id)

    @Test
    fun `borrow pujci dostupnou knihu a vrati novou vypujcku`() {
        val book = sampleBook(available = true)
        val user = sampleUser()

        // Arrange: naprogramujeme, co mocky vrátí, až je service zavolá.
        every { bookRepository.getByIdOrThrow(1L) } returns book
        every { userRepository.getByIdOrThrow(1L) } returns user
        // save(entity) v JPA vrací uloženou entitu; service návratovou hodnotu ignoruje,
        // tak jen vrátíme to, co dostal (firstArg() = první argument volání).
        every { loanRepository.save(any<Loan>()) } answers { firstArg() }

        // Act
        val loan = service.borrow(bookId = 1L, userId = 1L)

        // Assert — stav vzniklé výpůjčky
        assertSame(book, loan.book)                            // tatáž instance knihy (===)
        assertSame(user, loan.user)
        assertNull(loan.returnedAt)                            // ještě není vrácená
        assertEquals(loan.borrowedAt.plusDays(30), loan.dueAt) // splatnost = půjčeno + 30 dní

        // Assert — vedlejší efekt na knize
        assertFalse(book.available)                            // kniha je teď nedostupná

        // Assert — interakce s mockem: uložilo se to právě jednou
        verify(exactly = 1) { loanRepository.save(any<Loan>()) }
    }

    @Test
    fun `borrow na nedostupnou knihu vyhodi konflikt a nic neulozi`() {
        val book = sampleBook(available = false)
        every { bookRepository.getByIdOrThrow(1L) } returns book

        // assertFailsWith ověří TYP výjimky (a rovnou ji vrátí, kdybychom chtěli zkoumat message).
        assertFailsWith<BookNotAvailableException> {
            service.borrow(bookId = 1L, userId = 1L)
        }

        // Klíčové: service se má utnout hned po kontrole dostupnosti —
        // k userovi ani k uložení se vůbec nesmí dostat.
        verify(exactly = 0) { userRepository.getByIdOrThrow(any()) }
        verify(exactly = 0) { loanRepository.save(any<Loan>()) }
    }

    @Test
    fun `borrow propaguje vyjimku kdyz kniha neexistuje`() {
        // Když getByIdOrThrow vyhodí (což dělá i v reálu), service to nemá chytat.
        every { bookRepository.getByIdOrThrow(99L) } throws BookNotFoundException(99L)

        assertFailsWith<BookNotFoundException> {
            service.borrow(bookId = 99L, userId = 1L)
        }
        verify(exactly = 0) { loanRepository.save(any<Loan>()) }
    }

    @Test
    fun `returnLoan oznaci vraceni a uvolni knihu`() {
        val book = sampleBook(available = false) // půjčená kniha je nedostupná
        val loan = Loan(
            book = book,
            user = sampleUser(),
            borrowedAt = LocalDate.now().minusDays(3),
            dueAt = LocalDate.now().plusDays(27),
            id = 1L,
        )
        every { loanRepository.getByIdOrThrow(1L) } returns loan

        val result = service.returnLoan(loanId = 1L)

        assertNotNull(result.returnedAt) // datum vrácení se vyplnilo
        assertTrue(book.available)       // kniha je zase dostupná

        // returnLoan záměrně NEVOLÁ save — spoléhá na dirty checking (flush při commitu).
        // V unit testu bez DB proto ověřujeme jen mutaci entit, ne uložení.
        verify(exactly = 0) { loanRepository.save(any<Loan>()) }
    }

    @Test
    fun `returnLoan uz vracene vyhodi konflikt`() {
        val loan = Loan(
            book = sampleBook(),
            user = sampleUser(),
            borrowedAt = LocalDate.now().minusDays(5),
            dueAt = LocalDate.now().plusDays(25),
            returnedAt = LocalDate.now().minusDays(1), // už vrácená
            id = 1L,
        )
        every { loanRepository.getByIdOrThrow(1L) } returns loan

        assertFailsWith<LoanAlreadyReturnedException> {
            service.returnLoan(loanId = 1L)
        }
    }
}
