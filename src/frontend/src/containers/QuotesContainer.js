import { connect } from 'react-redux'
import Quotes from '../components/admin/Quotes'
import { addQuote, closeQuote, clearError, signalError } from '../actions'
import {fromJS, List, Map} from 'immutable'
import { browserHistory } from 'react-router'

const mapStateToProps = (state, ownProps) => {
	let quotes = state.get('quotes').toList()
	console.log(state.toJS())

	let r= {
		quotes,
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
			dispatch(addQuote(ownProps.params.id, data))
		},
		onCloseQuote: (data) => {
			dispatch(closeQuote(data))
		},
		onClearError: () => {
			dispatch(clearError())
		}
	}
}

const QuotesContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(Quotes)

export default QuotesContainer
