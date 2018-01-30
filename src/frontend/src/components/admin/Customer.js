import React from 'react'
import { Link } from 'react-router'
var ImmutablePropTypes = require('react-immutable-proptypes');

const Customer = ({onCreateTermDeposit, onListTermDeposits, onCreateBulkTermDeposit, customer}) => (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h2 className="display-4">Customer management</h2>
					<dl className="row">
						<dt className="col-md-3">Customer name</dt>
						<dd className="col-md-9">{customer.get('name')}</dd>
						<dt className="col-md-3">Location code</dt>
						<dd className="col-md-9">{customer.get('locationCode')}</dd>
					</dl>
					<form>
					  <div className="form-group">
						<button type="button" className="btn mr-3 btn-lg btn-block" onClick={()=>onListTermDeposits()}>Show Term Deposits</button>
						<button type="button" className="btn mr-3 btn-lg btn-block" onClick={()=>onCreateTermDeposit()}>Create Term Deposit</button>
						<button type="button" className="btn mr-3 btn-lg btn-block" onClick={()=>onCreateBulkTermDeposit()}>Create Bulk Term Deposits</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
)


export default Customer;
