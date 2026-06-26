package cz.zimi.library.book

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
    fun getByIdOrThrow(id: Long): Book =
        findById(id).orElseThrow { BookNotFoundException(id) }
}
