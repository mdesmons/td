import React from 'react'
import Alert from '../Alert'

class Onboard extends React.Component {
  constructor(props) {
	super(props)

	this.state =  {
		name: "",
		locationCode: "",
		cacheTDAccount: "",
		haircutAllowed: false,
		monthlyInterestAllowed: false,
		certificate: "",
		keystorePass: "",
		keyAlias: ""
	}

    this.onNameChange = this.onNameChange.bind(this);
    this.onLocationCodeChange = this.onLocationCodeChange.bind(this);
    this.onCacheTDAccountChange = this.onCacheTDAccountChange.bind(this);
    this.onHaircutAllowedChange = this.onHaircutAllowedChange.bind(this);
    this.onMonthlyInterestAllowedChange = this.onMonthlyInterestAllowedChange.bind(this);
    this.oncertificateChange = this.oncertificateChange.bind(this);
    this.onkeystorePassChange = this.onkeystorePassChange.bind(this);
    this.onKeyAliasChange = this.onKeyAliasChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

	onSubmit(event) {
		event.preventDefault()
		if (event.target.checkValidity()) {
			this.props.onSubmit(this.state)
		} else {
			event.target.classList.add('was-validated');
       	}
	}

	onNameChange(event) {
		this.setState({name : event.target.value})
	}

	onLocationCodeChange(event) {
		this.setState({locationCode : event.target.value})
	}

	onCacheTDAccountChange(event) {
		this.setState({cacheTDAccount : event.target.value})
	}

	onHaircutAllowedChange(event) {
		this.setState({haircutAllowed : event.target.checked})
	}

	onMonthlyInterestAllowedChange(event) {
		this.setState({monthlyInterestAllowed : event.target.checked})
	}
	oncertificateChange(event) {
		this.setState({certificate : event.target.value})
	}

	onkeystorePassChange(event) {
		this.setState({keystorePass : event.target.value})
	}

	onKeyAliasChange(event) {
		this.setState({keyAlias : event.target.value})
	}

	render() {

	return (
	<div className="container-fluid">
		<Alert error={this.props.error} onClearError={this.props.onClearError} />
		<div className="row">
			<div className="col-md-2"></div>
			<div className="col-md-8">
				<div className="bg-light rounded p-3">
					<h2>Onboard customer</h2>
					<p>This page lets you onboard a Cashactive Control customer on ANZ Term Deposits.</p>
					<form className="my-3" onSubmit={this.onSubmit} noValidate>
						<div className="form-group">
							<label htmlFor="name">Customer name</label>
							<input type="text" className="form-control" id="name" aria-describedby="emailHelp" placeholder="name" defaultValue= {this.state.name} onChange={this.onNameChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid customer name
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="locationCode">Cashactive location code</label>
							<input type="text" className="form-control" id="locationCode" aria-describedby="emailHelp" placeholder="000000" defaultValue= {this.state.locationCode} onChange={this.onLocationCodeChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid 6-digit location code
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="cacheTDAccount">Cache TD Account</label>
							<input type="text" className="form-control" id="cacheTDAccount" aria-describedby="emailHelp" placeholder="000000000" defaultValue= {this.state.cacheTDAccount} onChange={this.onCacheTDAccountChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid account number
							</div>
						</div>

						<div className="form-check">
							<input type="checkbox" className="form-check-inputEx" id="haircutAllowed" aria-describedby="emailHelp" onChange={this.onHaircutAllowedChange} />
							<label htmlFor="haircutAllowed" className="form-check-label">Haircut allowed</label>
						</div>

						<div className="form-check">
							<input type="checkbox" className="form-check-inputEx" id="monthlyInterestAllowed" aria-describedby="emailHelp" onChange={this.onMonthlyInterestAllowedChange} />
							<label htmlFor="monthlyInterestAllowed" className="form-check-label">Monthly interest allowed</label>
						</div>

						<div className="form-group">
							<label htmlFor="certificate">Certificate name</label>
							<input type="text" autoComplete="off" className="form-control" id="certificate" aria-describedby="emailHelp" placeholder="Provided by Fileactive" defaultValue= {this.state.certificate} onChange={this.oncertificateChange} required/>
							<div className="invalid-feedback">
								 Please enter a certificate name
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="keystorePass">Certificate password</label>
							<input type="password" autoComplete="off" className="form-control" id="keystorePass" aria-describedby="emailHelp" value= {this.state.keystorePass} onChange={this.onkeystorePassChange} required/>
							<div className="invalid-feedback">
								 Please enter a certificate password
							</div>
						</div>

						<div className="form-group">
							<label htmlFor="keyAlias">Certificate key alias</label>
							<input type="text" autoComplete="off" className="form-control" id="keyAlias" aria-describedby="emailHelp" placeholder="Provided by Fileactive" defaultValue= {this.state.keyAlias} onChange={this.onKeyAliasChange} required/>
							<div className="invalid-feedback">
								 Please enter a certificate key alias
							</div>
						</div>

						<button type="submit" className="btn btn-success mr-3">Create TD Customer</button>
					</form>
				</div>
			</div>
		</div>
	</div>
)}
}

export default Onboard;
