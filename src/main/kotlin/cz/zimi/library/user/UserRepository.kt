package cz.zimi.library.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun getByIdOrThrow(id: Long): User =
        findById(id).orElseThrow { UserNotFoundException(id) }
}
