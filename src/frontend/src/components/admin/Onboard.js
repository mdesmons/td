import React from 'react'
import Alert from '../Alert'

class Onboard extends React.Component {
  constructor(props) {
	super(props)

	this.state =  {
		name: "",
		locationCode: "",
		haircutAllowed: false
	}

    this.onNameChange = this.onNameChange.bind(this);
    this.onLocationCodeChange = this.onLocationCodeChange.bind(this);
    this.onHaircutAllowedChange = this.onHaircutAllowedChange.bind(this);
    this.onMarginChange = this.onMarginChange.bind(this);
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

	onHaircutAllowedChange(event) {
		this.setState({haircutAllowed : event.target.checked})
	}

	onMarginChange(event) {
		this.setState({margin : event.target.value})
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
							<label htmlFor="margin">Customer margin</label>
							<input type="number" className="form-control" id="margin" aria-describedby="emailHelp" placeholder="0.0" step="0.01" defaultValue= {this.state.margin} onChange={this.onMarginChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid 6-digit location code
							</div>
						</div>

						<div className="form-check">
							<input type="checkbox" className="form-check-inputEx" id="haircutAllowed" aria-describedby="emailHelp" onChange={this.onHaircutAllowedChange} />
							<label htmlFor="haircutAllowed" className="form-check-label">Haircut allowed</label>
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
