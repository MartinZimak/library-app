package cz.zimi.library.loan

import cz.zimi.library.common.ResourceNotFoundException

class LoanNotFoundException(id: Long) : ResourceNotFoundException("Zapujcka s id=$id neexistuje")
