package cz.zimi.library.loan

import cz.zimi.library.common.ConflictException

class LoanAlreadyReturnedException(id: Long) : ConflictException("Zapujcka s id=$id uz byla vracena")
