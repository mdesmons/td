import React from 'react'
import PropTypes from 'prop-types'
var ImmutablePropTypes = require('react-immutable-proptypes');

const CustomerItem = ({customer, onClick}) => (
		<tr  onClick={onClick}>
		<td>{customer.get('locationCode')}</td>
		<td>{customer.get('name')}</td>
		</tr>
)


const CustomerList = ({customers, onCustomerSelected}) => (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h1 className="display-4">Customers</h1>
					<table className="table table-hover">
						<thead>
							<tr>
								<th scope="col">Location Code</th>
								<th scope="col">Name</th>
							</tr>
						</thead>
						<tbody>
							{customers.map(cust => <CustomerItem key={cust.get('locationCode')} customer={cust} onClick={() => onCustomerSelected(cust.get('locationCode'))} />)}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
)



export default CustomerList;
