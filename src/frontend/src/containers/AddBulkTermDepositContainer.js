import { connect } from 'react-redux'
import AddBulkTermDeposit from '../components/admin/AddBulkTermDeposit'
import { addTermDeposit, clearError, calculateInterest, signalError } from '../actions'
import {fromJS, List, Map} from 'immutable'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {

	let r= {
	}

	return r
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		onError: (message) => {
			dispatch(signalError(fromJS({error: { message } })))
		},
		onSubmit: (data) => {
			dispatch(addTermDeposit(ownProps.params.id, data)).then(() => {browserHistory.push('/customer/' + ownProps.params.id)})
		},
		onRecalculateInterest: (data) => {
			dispatch(calculateInterest(ownProps.params.id, data))
		},
		onClearError: () => {
			dispatch(clearError())
		}
	}
}

const AddBulkTermDepositContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(AddBulkTermDeposit)

export default AddBulkTermDepositContainer
