import React from 'react'
import { Link } from 'react-router'

const CustomerDropDownItem = ({locationCode, name}) => (
	<a className="dropdown-item" href={'/customer/' + locationCode}>{name}</a>
)

const Toolbar = ({onLogout, customers}) => (
	<nav className="navbar navbar-expand-lg navbar-light bg-light">
	  <a className="navbar-brand" href="#">ANZ Term Deposits</a>
	  <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
		<span className="navbar-toggler-icon"></span>
	  </button>

	  <div className="collapse navbar-collapse" id="navbarSupportedContent">
		<ul className="navbar-nav mr-auto">
		  <li className="nav-item active">
			 <Link to="/home" className="nav-link" >Home</Link>
		  </li>
		  <li className="nav-item active">
			 <Link to="/onboard" className="nav-link" >Onboard customer</Link>
		  </li>
		  <li className="nav-item active">
			 <Link to="/customerList" className="nav-link" >Customer Maintenance</Link>
		  </li>
		  </ul>
			 <button className="btn btn-danger log" onClick={() => onLogout()}>Log out </button>
	  </div>
	</nav>
)

export default Toolbar;
