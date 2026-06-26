package cz.zimi.library.book

import cz.zimi.library.common.ConflictException

class BookNotAvailableException(id: Long) : ConflictException("Kniha s id=$id neni dostupna")
