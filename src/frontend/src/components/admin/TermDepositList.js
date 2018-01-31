import React from 'react'
import PropTypes from 'prop-types'
import { accountTypeForDisplay, accountStatusForDisplay } from '../../constants'
var ImmutablePropTypes = require('react-immutable-proptypes');

const TermDepositItem = ({termDeposit, onClick}) => (
		<tr  onClick={onClick}>
		<td>{termDeposit.get('sourceAccount')}</td>
		<td>{(new Date(termDeposit.get('valueDate'))).toLocaleString().substr(0, 10)}</td>
		<td>{termDeposit.get('principal').amount()}</td>
		<td>{termDeposit.get('interest') + '%'}</td>
		<td>{(new Date(termDeposit.get('maturityDate'))).toLocaleString().substr(0, 10)}</td>
		<td>{accountTypeForDisplay[termDeposit.get('paymentType')]}</td>
		<td>{accountStatusForDisplay[termDeposit.get('status')]}</td>
		</tr>
)

class TermDepositList extends React.Component {
  constructor(props) {
	super(props)
	this.state = {
		asc: true,
		sortField: 'valueDate'
	}

	this.data = Array.from(this.props.termDeposits)
    this.getTDList = this.getTDList.bind(this);
    this.swapOrder = this.swapOrder.bind(this);
    this.onHeaderClick = this.onHeaderClick.bind(this);
  }

	onHeaderClick(field) {
		if (this.state.sortField == field) {
			this.swapOrder()
		} else {
			this.setState({sortField: field})
			this.forceUpdate()
		}
	}

	swapOrder() {
		this.setState({asc : !this.state.asc})
		this.forceUpdate()
	}

	getTDList() {
		var self = this
		if (this.state.asc) {
			return this.data.sort(function(a, b) {
				if (a.get(self.state.sortField) == b.get(self.state.sortField)) return 0
				if (a.get(self.state.sortField) < b.get(self.state.sortField)) return 1
				if (a.get(self.state.sortField) > b.get(self.state.sortField)) return -1
				})
		} else {
			return this.data.sort(function(a, b) {
				if (a.get(self.state.sortField) == b.get(self.state.sortField)) return 0
				if (a.get(self.state.sortField) > b.get(self.state.sortField)) return 1
				if (a.get(self.state.sortField) < b.get(self.state.sortField)) return -1
				})
		}
	}


  render() {
	return (
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
								<th scope="col" onClick = {() => this.onHeaderClick('valueDate')}>Open Date</th>
								<th scope="col" onClick = {() => this.onHeaderClick('principal')}>Principal</th>
								<th scope="col">Int. Rate</th>
								<th scope="col" onClick = {() => this.onHeaderClick('maturityDate')}>Maturity</th>
								<th scope="col">Type</th>
								<th scope="col">Status</th>
							</tr>
						</thead>
						<tbody>
							{this.getTDList().map(td => <TermDepositItem key={td.get('id')} termDeposit={td} onClick={() => this.props.onTermDepositSelected(td.get('id'))} />)}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	)
	}
}



export default TermDepositList;
