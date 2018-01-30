import React from 'react'
import AdminHome from './admin/Home'
import StandardHome from './standard/Home'
import Login from './public/Home'
import {playerStatus} from '../constants'

const HomeImpl = ({connectionStatus, customers, onLogin}) => {
	console.log("User logged: " + connectionStatus.get('logged') + " scope: " + connectionStatus.get('scope'))
	if (connectionStatus.get('logged')) {
		if (connectionStatus.get('scope') === 'desk') return (<AdminHome/>)
		return (<StandardHome customers={customers}/>)
	} else {
		return (<Login onLogin={onLogin}/>)
	}
}

const Home = ({connectionStatus, customers, onLogin}) => {
	return (
		<HomeImpl connectionStatus={connectionStatus} onLogin={onLogin}/>
)}
export default Home;
