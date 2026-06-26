package cz.zimi.library.user

import cz.zimi.library.common.ResourceNotFoundException

class UserNotFoundException(id: Long) : ResourceNotFoundException("Uživatel s id=$id neexistuje")
