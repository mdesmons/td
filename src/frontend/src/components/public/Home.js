import React from 'react'
import Alert from '../Alert'

const Opponent = ({id, name}) => (
	<option value={id}>{name}</option>
)

class Login extends React.Component {
  constructor(props) {
	super(props)

	this.output = {
		username: "",
		password: ""
	}

    this.onUsernameChange = this.onUsernameChange.bind(this);
    this.onPasswordChange = this.onPasswordChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

	onSubmit(event) {
		event.preventDefault()
		if (event.target.checkValidity()) {
			this.props.onLogin(this.output)
		} else {
			event.target.classList.add('was-validated');
       	}
	}

	onUsernameChange(event) {
		// we receive the username as argument
		this.output.username = event.target.value
		this.forceUpdate()
	}
	onPasswordChange(event) {
		// we receive the username as argument
		this.output.password = event.target.value
		this.forceUpdate()
	}

	render() {

	return (
	<div className="container-fluid">
		<Alert error={this.props.error} onClearError={this.props.onClearError} />
		<div className="row">
			<div className="col-md-3"></div>
			<div className="col-md-6">
				<div className="bg-light rounded p-3">
					<h2>Login</h2>
					<form className="my-3" onSubmit={this.onSubmit} noValidate>
						<div className="form-group">
							<label htmlFor="username">User name</label>
							<input type="text" className="form-control" id="score" aria-describedby="emailHelp" placeholder="login" defaultValue= {this.output.username} onChange={this.onUsernameChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid login
							</div>
						</div>
						<div className="form-group">
							<label htmlFor="username">Password</label>
							<input type="password" className="form-control" id="score" aria-describedby="emailHelp" placeholder="login" defaultValue= {this.output.password} onChange={this.onPasswordChange} required/>
							<div className="invalid-feedback">
								 Please enter a valid password
							</div>
						</div>
						<button type="submit" className="btn btn-success mr-3">Login</button>
					</form>
				</div>
			</div>
		</div>
	</div>
)}
}

export default Login;
