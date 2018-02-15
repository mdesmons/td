import { connect } from 'react-redux'
import AddTermDeposit from '../components/admin/AddTermDeposit'
import { addTermDeposit, clearError, calculateInterest, signalError } from '../actions'
import {fromJS, List, Map} from 'immutable'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {
	let customer = state.getIn(['customers', ownProps.params.id])
	let accounts
	console.log(state.get('clientAccounts').toJS())
	accounts = state.get('clientAccounts').toList()

	let r= {
		customer,
		accounts,
		interestRate: state.get('interestRate'),
		error: state.get('error')
	}

	return r
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		onError: (message) => {
			dispatch(signalError(fromJS({error: { message } })))
		},
		onSubmit: (data) => {
			dispatch(addTermDeposit(ownProps.params.id, [data])).then(() => {browserHistory.push('/customer/' + ownProps.params.id)})
		},
		onRecalculateInterest: (data) => {
			dispatch(calculateInterest(ownProps.params.id, data))
		},
		onClearError: () => {
			dispatch(clearError())
		}
	}
}

const AddTermDepositContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(AddTermDeposit)

export default AddTermDepositContainer
