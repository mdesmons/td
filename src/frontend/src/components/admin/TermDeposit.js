import React from 'react'
import { Link } from 'react-router'
var ImmutablePropTypes = require('react-immutable-proptypes');

const TransferItem = ({transfer}) => (
		<tr>
		<td>{(new Date(transfer.get('date'))).toLocaleString().substr(0, 10)}</td>
		<td>{transfer.get('narrative')}</td>
		<td>{transfer.get('amount')}</td>
		</tr>
)

const TermDeposit = ({onClose, onNoticePeriodClose, onHardshipClose, termDeposit, transfers}) => (
	<div className="container mt-3">
		<div className="row">
			<div className="col bg-light rounded p-3">
				<h2>Term Deposit Details</h2>
				<dl className="row">
					<dt className="col-md-3">Source account</dt>
					<dd className="col-md-9">{termDeposit.get('sourceAccount')}</dd>
					<dt className="col-md-3">Principal amount</dt>
					<dd className="col-md-9">{termDeposit.get('principal')}</dd>
					<dt className="col-md-3">Term</dt>
					<dd className="col-md-9">{termDeposit.get('term')}</dd>
					<dt className="col-md-3">Interest rate</dt>
					<dd className="col-md-9">{termDeposit.get('interestRate')}</dd>
					<dt className="col-md-3">Haircut rate</dt>
					<dd className="col-md-9">{termDeposit.get('haircutRate')}</dd>
					<dt className="col-md-3">Monthly interest</dt>
					<dd className="col-md-9">{termDeposit.get('monthlyInterest')}</dd>
				</dl>
			</div>
		</div>
		<div className="row">
			<div className="col bg-light rounded p-3 mt-1">
				<h2>Transactions</h2>
				<table className="table table-hover">
					<thead>
						<tr>
							<th scope="col">Date</th>
							<th scope="col">Type</th>
							<th scope="col">Amount</th>
						</tr>
					</thead>
					<tbody>
						{transfers.map(transfer => <TransferItem key={transfer.get('id')} transfer={transfer}  />)}
					</tbody>
				</table>
			</div>
		</div>
		<div className="row">
			<div className="col bg-light rounded p-3 mt-1"><h2>Actions</h2>
				<form>
				  <div className="form-group">
					<button type="button" className="btn mr-3" onClick={()=>onClose()}>Close Term Deposit</button>
					<button type="button" className="btn mr-3" onClick={()=>onNoticePeriodClose()}>Notice Period Close Term Deposit</button>
					<button type="button" className="btn mr-3" onClick={()=>onHardshipClose()}>Hardship Close Term Deposit</button>
					</div>
				</form>
			</div>
		</div>
	</div>
)


export default TermDeposit;
