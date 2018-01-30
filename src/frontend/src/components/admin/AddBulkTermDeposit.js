import React from 'react'
import Alert from '../Alert'
import Papa from 'papaparse'
import {accountTypeForDisplay} from '../../constants'

const TermDepositItem = ({termDeposit, onClick}) => (
		<tr  onClick={onClick}>
		<td>{termDeposit.key}</td>
		<td>{termDeposit.sourceAccount}</td>
		<td>{termDeposit.principal.amount()}</td>
		<td>{termDeposit.term != 0?termDeposit.term + ' days':''}</td>
		<td>{termDeposit.term == 0? (new Date(termDeposit.maturity)).toLocaleString().substr(0, 10) : ''}</td>
		<td>{termDeposit.haircut + '%'}</td>
		<td>{accountTypeForDisplay[termDeposit.paymentType]}</td>
		</tr>
)

class AddBulkTermDeposit extends React.Component {
  constructor(props) {
	super(props)
	this.state =  {
		termDeposits: []
	}

    this.onFileChosen = this.onFileChosen.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

	onFileChosen(event) {
		var self = this
		console.log(event.target.files[0])
		Papa.parse(event.target.files[0], {
        	complete: function(results) {
				self.setState({termDeposits : results.data.map(function(it) { return {
					key: it[0],
					sourceAccount: it[1],
					principal: Number(it[2]),
					term: Number(it[3]),
					maturity: it[3] == ""?(new Date(it[4])).getTime():"",
					haircut: it[5]==""?0:Number(it[5]),
					paymentType: Number(it[6])
				}})})
        	}
        });

	//	this.props.onFileChosen(file)
	}

	onSubmit(event) {
		event.preventDefault()
		this.props.onSubmit(this.state.termDeposits)
	}

	render() {

	return (
	<div className="container-fluid">
		<Alert error={this.props.error} onClearError={this.props.onClearError} />
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h2 className="display-4">Create Bulk Term Deposit</h2>
					<p>This page lets you upload a customer-provided file and create easily large numbers of term deposits.</p>
					<form className="my-3">
						<div className="custom-file">
						  <label htmlFor="customFile">Please select a file to upload</label>
						  <input type="file" className="form-control-file" id="customFile" onChange={this.onFileChosen} />
						</div>
					</form>
				</div>
			</div>
		</div>
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h1 className="display-4">Term Deposits</h1>
					<table className="table table-striped">
						<thead>
							<tr>
								<th scope="col">Id</th>
								<th scope="col">Source Account</th>
								<th scope="col">Principal</th>
								<th scope="col">Term</th>
								<th scope="col">Maturity</th>
								<th scope="col">Haircut</th>
								<th scope="col">Type</th>
							</tr>
						</thead>
						<tbody>
							{this.state.termDeposits.map(td => <TermDepositItem key={td.key} termDeposit={td} />)}
						</tbody>
					</table>
					<form className="my-3" onSubmit={this.onSubmit} noValidate>
						<button type="submit" className="btn btn-success mr-3" disabled={this.state.termDeposits.length == 0}>Create Term Deposits</button>
					</form>
				</div>
			</div>
		</div>
	</div>
)}
}

export default AddBulkTermDeposit;
