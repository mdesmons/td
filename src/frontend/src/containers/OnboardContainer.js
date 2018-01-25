import { connect } from 'react-redux'
import Onboard from '../components/admin/Onboard'
import { onboard, clearError } from '../actions'
import {fromJS} from 'immutable'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {
	return {}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		onSubmit: (data) => {
			dispatch(onboard(data)).then(() => {browserHistory.push('/')})
		},
		onClearError: () => {
			dispatch(clearError())
		}
	}
}

const OnboardContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(Onboard)

export default OnboardContainer
