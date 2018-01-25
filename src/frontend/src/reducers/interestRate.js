import { fromJS } from 'immutable';
const initialInterest = fromJS([{value: 5.5}])

const interestRate = (state = initialInterest, action) => {
  	console.log("entering interest reducer")

	switch (action.type) {
		case 'CALCULATE_INTEREST':
			return action.data.getIn(['interestRate', '0'])
		default:
			return state;
	}
}

export default interestRate
