import { connect } from 'react-redux'
import { signalError, clearError, closeTermDeposit } from '../actions'
import {fromJS} from 'immutable'
import TermDeposit from '../components/admin/TermDeposit'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {
	let termDeposit = state.getIn(['termDeposits', ownProps.params.id])
	let transferIds = termDeposit.get('transfers')
	let transfers = transferIds.map(id => state.getIn(['transfers', String(id)]))
	console.log(transfers.toJS())

	let r= {
		termDeposit,
		transfers,
		error: state.get('error')
	}

	return r
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		onClose: () => { browserHistory.push('/customer/' + ownProps.params.customerId);; dispatch(closeTermDeposit(ownProps.params.id, {reason: 4})).then(() => { })},
		onNoticePeriodClose: () => { dispatch(closeTermDeposit(ownProps.params.id, {reason: 2})).then(() => {browserHistory.push('/customer/' + ownProps.params.customerId)})},
		onHardshipClose: () => { dispatch(closeTermDeposit(ownProps.params.id, {reason: 3})).then(() => {browserHistory.push('/customer/' + ownProps.params.customerId)})},
		onClearError: () => { dispatch(clearError()) }
	}
}

const TermDepositContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(TermDeposit)

export default TermDepositContainer
