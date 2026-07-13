package cz.zimi.library.loan

import cz.zimi.library.book.Book
import cz.zimi.library.book.BookRepository
import cz.zimi.library.user.User
import cz.zimi.library.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.AfterTest
import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Integrační test LoanService proti REÁLNÉ H2 (žádné mocky).
 *
 * @SpringBootTest nabootuje celý Spring kontext + datasource (H2, create-drop),
 * takže si přes konstruktor necháme injectnout skutečné beany (service + repozitáře).
 *
 * Záměrně BEZ @Transactional na testu → service běží ve své vlastní @Transactional
 * a opravdu COMMITNE. (Kdybychom dali @Transactional sem, celý test by běžel v jedné
 * transakci a na konci se rollbacknul — nikdy bychom neověřili reálný zápis do DB.)
 */
@SpringBootTest
class LoanServiceIntegrationTest @Autowired constructor(
    private val loanService: LoanService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val loanRepository: LoanRepository,
) {

    // H2 je sdílená napříč testy v této třídě (bez rollbacku si data neseme dál),
    // tak po každém testu uklidíme. Pořadí kvůli FK: nejdřív loans (odkazují na
    // books/users), teprve pak knihy a uživatelé.
    @AfterTest
    fun cleanup() {
        loanRepository.deleteAll()
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `borrow zapise available=false do databaze`() {
        // Arrange: reálně uložíme knihu a uživatele (committnuté řádky v H2).
        // V testech je !! v pohodě — když by id po save bylo null, chceme hlasitý pád.
        val book = bookRepository.save(Book(title = "Hobit", author = "Tolkien", isbn = "9788025707418"))
        val user = userRepository.save(User(firstName = "Martin", lastName = "Zimak", email = "martin@example.com"))

        // Act: borrow poběží ve své @Transactional a při commitu se přes dirty checking
        // vygeneruje UPDATE books SET available=false.
        val loan = loanService.borrow(bookId = book.id!!, userId = user.id!!)

        // Assert: FRESH čtení z DB (nová transakce, nová instance) — ne ten objekt,
        // který držíme v ruce. Právě tohle unit test s mockem nedokázal:
        // kdyby dirty checking UPDATE nezapsal, tady by available byla pořád true.
        val reloaded = bookRepository.findById(book.id!!).get()
        assertFalse(reloaded.available)

        // a výpůjčka se opravdu uložila
        assertNotNull(loanRepository.findById(loan.id!!).orElse(null))
    }

    @Test
    fun `return zapise available=true do databaze`() {
        val book = bookRepository.save(Book(title = "Hobit", author = "Tolkien", isbn = "9788025707418"))
        val user = userRepository.save(User(firstName = "Martin", lastName = "Zimak", email = "martin@example.com"))
        val loan = loanService.borrow(bookId = book.id!!, userId = user.id!!)

        loanService.returnLoan(loanId = loan.id!!)

        val reloadedBook = bookRepository.findById(book.id!!).get()
        assertTrue(reloadedBook.available)

        val reloadedLoan = loanRepository.findById(loan.id!!).get()
        assertNotNull(reloadedLoan.returnedAt)
    }
}
