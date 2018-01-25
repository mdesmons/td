import { connect } from 'react-redux'
import { signalError, clearError } from '../actions'
import {fromJS} from 'immutable'
import Customer from '../components/admin/Customer'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {
	let customer = state.getIn(['customers', ownProps.params.id])

	let r= {
		customer,
		error: state.get('error')
	}

	return r
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		onCreateTermDeposit: () => { browserHistory.push("/addTermDeposit/" + ownProps.params.id)},
		onListTermDeposits: () => { browserHistory.push("/termDepositList")},
		onClearError: () => { dispatch(clearError()) }
	}
}

const CustomerContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(Customer)

export default CustomerContainer
