import React from 'react'
import PropTypes from 'prop-types'
var ImmutablePropTypes = require('react-immutable-proptypes');

const TermDepositItem = ({termDeposit, onClick}) => (
		<tr  onClick={onClick}>
		<td>{termDeposit.get('sourceAccount')}</td>
		<td>{(new Date(termDeposit.get('valueDate') * 1000)).toLocaleString().substr(0, 10)}</td>
		<td>{termDeposit.get('principal').amount()}</td>
		<td>{termDeposit.get('interest')}</td>
		<td>{termDeposit.get('term')}</td>
		<td>{(new Date(termDeposit.get('maturityDate'))).toLocaleString().substr(0, 10)}</td>
		<td>{termDeposit.get('monthlyInterest')}</td>
		</tr>
)


const TermDepositList = ({termDeposits, onTermDepositSelected}) => (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h1 className="display-4">Term Deposits</h1>
					<table className="table table-hover">
						<thead>
							<tr>
								<th scope="col">Source Account</th>
								<th scope="col">Value Date</th>
								<th scope="col">Principal</th>
								<th scope="col">Interest Rate</th>
								<th scope="col">Term</th>
								<th scope="col">Maturity</th>
								<th scope="col">Type</th>
							</tr>
						</thead>
						<tbody>
							{termDeposits.map(td => <TermDepositItem key={td.get('id')} termDeposit={td} onClick={() => onTermDepositSelected(td.get('id'))} />)}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
)



export default TermDepositList;
