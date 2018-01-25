import React from 'react'
import { Link } from 'react-router'
var ImmutablePropTypes = require('react-immutable-proptypes');

const Customer = ({onCreateTermDeposit, onListTermDeposits, subscription}) => (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">	<h2>Customer management</h2>
					<form>
					  <div className="form-group">
						<button type="button" className="btn mr-3" onClick={()=>onListTermDeposits()}>Show Term Deposits</button>
						<button type="button" className="btn mr-3" onClick={()=>onCreateTermDeposit()}>Create Term Deposit</button>
						<button type="button" className="btn mr-3">Create Bulk Term Deposits</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
)


export default Customer;
