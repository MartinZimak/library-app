package cz.zimi.library.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
) {

    fun getAll(): List<User> =
        repository.findAll()

    fun getById(id: Long): User =
        repository.getByIdOrThrow(id)

    fun create(request: UserRequest): User {
        val existingByEmail = repository.findByEmail(request.email)

        if (existingByEmail != null) {
            throw EmailAlreadyExistsException(request.email)
        }

        return repository.save(request.toEntity())
    }

    fun update(id: Long, request: UserRequest): User {
        val existing = getById(id)

        val existingByEmail = repository.findByEmail(request.email)
        if (existingByEmail != null && existingByEmail.id != id) {
            throw EmailAlreadyExistsException(request.email)
        }

        existing.firstName = request.firstName
        existing.lastName = request.lastName
        existing.email = request.email
        return repository.save(existing)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw UserNotFoundException(id)
        }
        repository.deleteById(id)
    }
}
