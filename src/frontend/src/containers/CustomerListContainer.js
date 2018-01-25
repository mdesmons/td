import { connect } from 'react-redux'
import CustomerList from '../components/admin/CustomerList'
import { browserHistory } from 'react-router'
import {selectCustomer} from '../actions'

const mapStateToProps = state => {
	let customers = state.get('customers').toList()

	return {
		customers
	}
}

const mapDispatchToProps = dispatch => {
	return {
		onCustomerSelected: id => {
			console.log("Customer selected")
			dispatch(selectCustomer(id)).then(() => {browserHistory.push('/customer/' + id)})
		}
	}
}

const CustomerListContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(CustomerList)

export default CustomerListContainer
