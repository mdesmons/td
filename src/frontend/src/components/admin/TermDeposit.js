import React from 'react'
import { Link } from 'react-router'
import { accountTypeForDisplay, transferStatus } from '../../constants'
var ImmutablePropTypes = require('react-immutable-proptypes');

const TransferItem = ({transfer}) => (
		<tr>
		<td>{(new Date(transfer.get('date'))).toLocaleString().substr(0, 10)}</td>
		<td>{transfer.get('narrative')}</td>
		<td className="text-right">{transfer.get('amount').amount()}</td>
		</tr>
)

const TermDeposit = ({onClose, onNoticePeriodClose, onHardshipClose, termDeposit, transfers}) => (
	<div className="container mt-3">
		<div className="row">
			<div className="col bg-light rounded p-3">
				<h2 className="display-4">Term Deposit Details</h2>
				<dl className="row">
					<dt className="col-md-3">Source account</dt>
					<dd className="col-md-9">{termDeposit.get('sourceAccount')}</dd>
					<dt className="col-md-3">Principal amount</dt>
					<dd className="col-md-9">{termDeposit.get('principal').amount()}</dd>
					<dt className="col-md-3">Term</dt>
					<dd className="col-md-9">{termDeposit.get('term') + " days"}</dd>
					<dt className="col-md-3">Interest rate</dt>
					<dd className="col-md-9">{termDeposit.get('interest').percent() + "%"}</dd>
					<dt className="col-md-3">Haircut rate</dt>
					<dd className="col-md-9">{termDeposit.get('haircut').percent() + "%"}</dd>
					<dt className="col-md-3">Account type</dt>
					<dd className="col-md-3">{accountTypeForDisplay[termDeposit.get('paymentType')]}</dd>
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
							<th scope="col" className="text-right">Amount</th>
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
					<button type="button" className="btn mr-3" onClick={()=>onClose()} disabled={termDeposit.get('status')!=transferStatus.active}>Close Term Deposit</button>
					<button type="button" className="btn mr-3" onClick={()=>onNoticePeriodClose()} disabled={termDeposit.get('status')!=transferStatus.active}>Notice Period Close Term Deposit</button>
					<button type="button" className="btn mr-3" onClick={()=>onHardshipClose()} disabled={termDeposit.get('status')!=transferStatus.active}>Hardship Close Term Deposit</button>
					</div>
				</form>
			</div>
		</div>
	</div>
)


export default TermDeposit;
