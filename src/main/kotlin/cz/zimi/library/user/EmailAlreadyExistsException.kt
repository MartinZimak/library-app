package cz.zimi.library.user

import cz.zimi.library.common.ConflictException

class EmailAlreadyExistsException(email: String) : ConflictException("Uživatel s emailem $email už existuje")
