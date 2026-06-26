package cz.zimi.library.book

import cz.zimi.library.common.ResourceNotFoundException

class BookNotFoundException(id: Long) : ResourceNotFoundException("Kniha s id=$id neexistuje")
