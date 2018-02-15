import React from 'react'
import Alert from '../Alert'

const Account = ({id, name}) => (
	<option value={id}>{id + " " + name}</option>
)

class AddTermDeposit extends React.Component {
  constructor(props) {
	super(props)
	this.state =  {
			principal: "",
			interestRate: 0.0,
			haircut: 0.0,
			monthlyInterest: false,
			termDays: "",
			termMonths: "",
			sourceAccount: props.accounts.first().get('id'),
			maturityDate: "",
			overrideRate : false
		}

    this.onPrincipalChange = this.onPrincipalChange.bind(this);
    this.onInterestRateChange = this.onInterestRateChange.bind(this);
    this.onHaircutChange = this.onHaircutChange.bind(this);
    this.onMonthlyInterestChange = this.onMonthlyInterestChange.bind(this);
    this.onTermMonthsChange = this.onTermMonthsChange.bind(this);
    this.onTermDaysChange = this.onTermDaysChange.bind(this);
    this.onSourceAccountChange = this.onSourceAccountChange.bind(this);
    this.onMaturityDateChange = this.onMaturityDateChange.bind(this);
    this.onOverrideRateChange = this.onOverrideRateChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }


	onSubmit(event) {
		event.preventDefault()
		if (event.target.checkValidity()) {
			var output = {
				principal: this.state.principal,
				sourceAccount: this.state.sourceAccount
			}

			if (this.state.haircut != 0) {
				output.haircut = this.state.haircut
			}

			if (this.state.overrideRate) {
				output.interest = this.state.interestRate
			}

			if (this.state.termDays != "") {
				output.term = this.state.termDays
			} else if (this.state.termMonths != "") {
				output.term = this.state.termMonths * 30
			} else if (this.state.maturityDate != "") {
				// convert the date to seconds from the epoch
				output.maturity = (new Date(this.state.maturityDate)).getTime()
			} else {
				this.props.onError("A Term in day/month or a Maturity Date must be entered")
				return
			}

			if (this.state.monthlyInterest) {
				output.paymentType = 1
			} else {
				output.paymentType = 0
			}

			this.props.onSubmit(output)
		} else {
			event.target.classList.add('was-validated');
       	}
	}

	onPrincipalChange(event) {
		this.setState({principal : event.target.value})
//		console.log(this.state)
//		this.props.onRecalculateInterest(this.state)
	}

	onInterestRateChange(event) {
		this.setState({interestRate : event.target.value})
	}

	onHaircutChange(event) {
		this.setState({haircut : event.target.value})
	}

	onMonthlyInterestChange(event) {
		this.setState({monthlyInterest : event.target.checked})
//		this.props.onRecalculateInterest(this.state)
	}

	onOverrideRateChange(event) {
		if (!event.target.checked) {
			this.setState({interestRate : 0})
		}

//		this.props.onRecalculateInterest(this.state)
		this.setState({overrideRate : event.target.checked})
	}

	onTermMonthsChange(event) {
		this.setState({termMonths : event.target.value, termDays: "", maturityDate: ""})
//		this.props.onRecalculateInterest(this.state)
	}

	onTermDaysChange(event) {
		this.setState({termDays : event.target.value, termMonths: "", maturityDate: ""})
//		this.props.onRecalculateInterest(this.state)
	}

	onMaturityDateChange(event) {
		this.setState({maturityDate : event.target.value, termMonths: "", termDays:""})
//		this.props.onRecalculateInterest(this.state)
	}

	onSourceAccountChange(event) {
		this.setState({sourceAccount : event.target.value})
	}

	componentDidUpdate(prevProps, prevState) {
		if ((this.state.termMonths != prevState.termMonths) ||
		(this.state.termDays != prevState.termDays) ||
		(this.state.principal != prevState.principal) ||
		(this.state.maturityDate != prevState.maturityDate) ||
		(this.state.monthlyInterest != prevState.monthlyInterest)) {

			var output = {
				principal: this.state.principal,
			}

			if (this.state.termDays != "") {
				output.term = this.state.termDays
			} else if (this.state.termMonths != "") {
				output.term = this.state.termMonths * 30
			} else if (this.state.maturityDate != "") {
				// convert the date to seconds from the epoch
				output.maturity = (new Date(this.state.maturityDate)).getTime()
			} else {
				return
			}

			if (this.state.monthlyInterest) {
				output.paymentType = 1
			} else {
				output.paymentType = 0
			}

			this.props.onRecalculateInterest(output)
		}
	}

	render() {

	return (
	<div className="container-fluid">
		<Alert error={this.props.error} onClearError={this.props.onClearError} />
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h2 className="display-4">Create Term Deposit</h2>
					<p>This page lets you create a Term Deposit for a Cashactive Control customer.</p>
					<form className="my-3" onSubmit={this.onSubmit} noValidate>
						<div className="form-group">
							<label htmlFor="sourceAccount">Source Client Account</label>
							<select className="form-control" id="sourceAccount"  onChange={this.onSourceAccountChange} value= {this.state.sourceAccount}>
							{this.props.accounts.map(account => <Account key={account.get('id')} id={account.get('id')} name={account.get('name')}  />)}
							</select>
						</div>

						<div className="form-group">
							<label htmlFor="principal">Principal</label>
							<input type="number" step="0.01" className="form-control" id="principal" placeholder="Please enter a principal amount" value= {this.state.principal} onChange={this.onPrincipalChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid amount. Must be positive and less than the account balance
							</div>
						</div>
						
						<div className="form-group">
							<label htmlFor="calculatedRate">Proposed Interest rate</label>
							<input type="number" min="0" className="form-control" id="calculatedRate" value= {this.props.interestRate.get('value')} readOnly/>
							<div className="invalid-feedback">
								 Please enter a valid rate
							</div>
						</div>

						<div className="form-check">
							<input type="checkbox" className="form-check-inputEx" id="overrideRate" onChange={this.onOverrideRateChange}  />
							<label htmlFor="overrideRate" className="form-check-label">override proposed rate</label>
						</div>
						<div className="form-group">
							<input type="number" step="0.01" min="0" className="form-control" id="interestRate" value= {this.state.interestRate} onChange={this.onInterestRateChange} disabled={!this.state.overrideRate}/>
						</div>

						<div className="form-group">
							<label htmlFor="termDays">Term (days)</label>
							<input type="number" min="0" className="form-control" id="termDays" value= {this.state.termDays} onChange={this.onTermDaysChange}/>
							<div className="invalid-feedback">
								 Please enter a valid term
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="termMonths"><u><b>OR</b></u> Term (months)</label>
							<input type="number" min="0" className="form-control" id="termMonths" value= {this.state.termMonths} onChange={this.onTermMonthsChange}/>
							<div className="invalid-feedback">
								 Please enter a valid term
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="maturityDate"><u><b>OR</b></u> Maturity Date</label>
							<input type="date" className="form-control" id="term" value= {this.state.maturityDate} onChange={this.onMaturityDateChange}/>
							<div className="invalid-feedback">
								 Please enter a valid maturity date
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="haircut">Haircut rate</label>
							<input type="number" step="0.01" min="0" className="form-control" id="haircut" placeholder="0.00" defaultValue= {this.state.haircut} onChange={this.onHaircutChange} disabled={!this.props.customer.get('haircutAllowed')}/>
							<div className="invalid-feedback">
								 Please enter a valid rate
							</div>
						</div>

						<div className="form-check">
							<input type="checkbox" className="form-check-inputEx" id="monthlyInterest" onChange={this.onMonthlyInterestChange}  disabled={!this.props.customer.get('monthlyInterestAllowed')}/>
							<label htmlFor="monthlyInterest" className="form-check-label">Monthly interest</label>
						</div>

						<button type="submit" className="btn btn-success mr-3">Create Term Deposit</button>
					</form>
				</div>
			</div>
		</div>
	</div>
)}
}

export default AddTermDeposit;
