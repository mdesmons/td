import { connect } from 'react-redux'
import { signalError, clearError } from '../actions'
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
		onClose: () => { console.log("Closing of TD")},
		onNoticePeriodClose: () => { console.log("Notice Period Closing of TD")},
		onHardshipClose: () => { console.log("Hardship Closing of TD")},
		onClearError: () => { dispatch(clearError()) }
	}
}

const TermDepositContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(TermDeposit)

export default TermDepositContainer
