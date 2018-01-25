export const playerStatus = {
	emailUnverified: "emailUnverified",
	profileIncomplete: "profileIncomplete",
	active: "active",
	suspended: "suspended",
	closed: "closed",
	membershipExpired: "membershipExpired"
}

export const playerStatusString = {
	emailUnverified: "Unverified",
	profileIncomplete: "Signed up",
	active: "Active",
	suspended: "Suspended",
	membershipExpired: "Membership expired"
}


export const playerGender = {
	any: 0,
	female: 1,
	male: 2,
	other: 3
}

export const playerGenderString = {
	0: "",
	1: "Female",
	2: "Male",
	3: "Other"
}

	export const playerGenderStringForDisplay = {
	0: "Any",
	1: "Female",
	2: "Male",
	3: "Other"
}

export const stripeKey = "pk_test_MvbZbHGNZL3ULMc5vPHQD9Bt"

String.prototype.asPhone = function(str){
	return (this.substring(0, 4) + ' ' + this.substring(4, 7) + ' ' + this.substring(7, 10))
}
