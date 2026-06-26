package cz.zimi.library.loan

import cz.zimi.library.book.Book
import cz.zimi.library.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "loans")
class Loan(

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    var book: Book,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(nullable = false)
    var borrowedAt: LocalDate,

    @Column(nullable = false)
    var dueAt: LocalDate,

    @Column(nullable = true)
    var returnedAt: LocalDate? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)