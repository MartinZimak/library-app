package cz.zimi.library.book

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Co klient POSÍLÁ při vytvoření/úpravě knihy (bez id — to přiděluje DB).
data class BookRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:NotBlank
    @field:Size(max = 255)
    val author: String,

    @field:NotBlank
    @field:Size(max = 20, message = "ISBN smí mít nejvýše {max} znaků")
    val isbn: String,

    val available: Boolean = true,
)

// Co klientovi VRACÍME (id už existuje, entitu ven neposíláme).
data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val available: Boolean,
)

// --- Mapování (Kotlin extension funkce) ---

fun Book.toResponse(): BookResponse = BookResponse(
    id = id ?: error("Ulozena kniha musi mit id"),
    title = title,
    author = author,
    isbn = isbn,
    available = available,
)

fun BookRequest.toEntity(): Book = Book(
    title = title,
    author = author,
    isbn = isbn,
    available = available,
)
