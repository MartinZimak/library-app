package cz.zimi.library.book

import org.springframework.stereotype.Service

@Service
class BookService(
    private val repository: BookRepository,
) {

    fun getAll(): List<Book> =
        repository.findAll()

    fun getById(id: Long): Book =
        repository.getByIdOrThrow(id)

    fun create(request: BookRequest): Book =
        repository.save(request.toEntity())

    fun update(id: Long, request: BookRequest): Book {
        val existing = getById(id)
        existing.title = request.title
        existing.author = request.author
        existing.isbn = request.isbn
        existing.available = request.available
        return repository.save(existing)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw BookNotFoundException(id)
        }
        repository.deleteById(id)
    }
}
