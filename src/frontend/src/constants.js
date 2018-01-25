export const accountTypeForDisplay = {
	0: "At Call",
	1: "Monthly interest payment"
}

String.prototype.asPhone = function(str){
	return (this.substring(0, 4) + ' ' + this.substring(4, 7) + ' ' + this.substring(7, 10))
}


Number.prototype.amount = function() {
  return this.toLocaleString({},
  	{
  		currency:'AUD',
  		minimumFractionDigits:2,
  		maximumFractionDigits:2,
  		currencyDisplay:"code",
  		style:"currency"
  	})

}
