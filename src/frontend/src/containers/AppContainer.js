import { connect } from 'react-redux'
import AppNg from '../components/AppNg'
import { login, logout } from '../AuthService';
import { logoutAction } from '../actions'
import { browserHistory } from 'react-router';

const mapStateToProps = (state, props) => {
	return {
		customers: state.get('customers').toList(),
		termDeposits: state.get('termDeposits').toList(),
		connectionStatus: state.get('connectionStatus'),
		children: props.children
	}
}

const mapDispatchToProps = dispatch => {
	return {
		onLogout : () => {
			logout()
			dispatch(logoutAction())
  			browserHistory.replace('/home');
		},
	}
}

const AppContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(AppNg)

export default AppContainer
