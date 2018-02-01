export const accountTypeForDisplay = {
	0: "At Call",
	1: "Monthly payment"
}

export const accountStatusForDisplay = {
	0: "Active",
	1: "Pending close"
}

export const accountStatus = {
	active: 0,
	cancelled: 1
}

export const transferStatus = {
	active: 0,
	pendingClose: 1,
	closed: 2
}

String.prototype.asPhone = function(str){
	return (this.substring(0, 4) + ' ' + this.substring(4, 7) + ' ' + this.substring(7, 10))
}

Number.prototype.percent = function() {
  return this.toLocaleString({},
  	{
  		minimumFractionDigits:2,
  		maximumFractionDigits:2,
  		style:"decimal"
  	})

}

Number.prototype.amount = function() {
  return this.toLocaleString({},
  	{
  		currency:'AUD',
  		minimumFractionDigits:2,
  		maximumFractionDigits:2,
  		currencyDisplay:"symbol",
  		style:"currency"
  	})

}
