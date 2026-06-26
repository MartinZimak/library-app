package cz.zimi.library.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Email

data class UserRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 255)
    val lastName: String,

    @field:NotBlank
    @field:Email
    val email: String,
)

data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
)

fun User.toResponse(): UserResponse = UserResponse(
    id = id ?: error("Ulozeny uzivatel musi mit id"),
    firstName = firstName,
    lastName = lastName,
    email = email,
)

fun UserRequest.toEntity(): User = User(
    firstName = firstName,
    lastName = lastName,
    email = email,
)
