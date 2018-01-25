import { connect } from 'react-redux'
import TermDepositList from '../components/admin/TermDepositList'
import { browserHistory } from 'react-router'
import {selectTermDeposit} from '../actions'

const mapStateToProps = state => {
	let termDeposits = state.get('termDeposits').toList()

	return {
		termDeposits
	}
}

const mapDispatchToProps = dispatch => {
	return {
		onTermDepositSelected: id => {
			browserHistory.push('/termDeposit/' + id)
		}
	}
}

const TermDepositListContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(TermDepositList)

export default TermDepositListContainer
