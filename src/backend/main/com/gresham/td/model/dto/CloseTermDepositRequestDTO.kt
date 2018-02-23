package com.gresham.td.model.dto

import com.gresham.td.model.TermDepositCloseReason


class CloseTermDepositRequestDTO(
		val reason: TermDepositCloseReason = TermDepositCloseReason.None
)
