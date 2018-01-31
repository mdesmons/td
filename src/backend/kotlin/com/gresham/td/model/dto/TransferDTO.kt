package com.gresham.td.model.dto

import com.gresham.td.model.Transfer

class TransferDTO(transfer: Transfer) {
	val id = transfer.id
	val type = transfer.type
	val currency = transfer.currency
	val amount = transfer.amount
	val date = transfer.date
	val narrative = transfer.narrative
	val status = transfer.status
}
