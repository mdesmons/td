import React from 'react'
import AdminToolbar from './admin/Toolbar'
import StandardToolbar from './standard/Toolbar'
import TopBanner from './TopBanner'
import PublicToolbar from './public/Toolbar'
import { Link } from 'react-router'

const Toolbar = ({customers, connectionStatus, onLogin, onLogout}) => {
	if (connectionStatus.get('logged')) {
		if (connectionStatus.get('scope') === 'desk') {
			return (<div>
			<AdminToolbar onLogout={onLogout} customers={customers}/>
			<TopBanner message={"Admin console"} />
			</div>
			)
		} else {
			return <StandardToolbar onLogout={onLogout}/>
		}
	}
	return <PublicToolbar onLogin={onLogin}/>
}

const AppNg = ({customers, connectionStatus, children, onLogin, onLogout}) =>
	(
		<div>
			<header>
			<Toolbar customers={customers} connectionStatus={connectionStatus} onLogin={onLogin} onLogout={onLogout}/>
			</header>
			<main style={{marginBottom:120,marginTop:20}}>
			{children}
			</main>
			<footer className="footer">
				<div className="container">
					<div className="row d-flex flex-row-reverse">
						<ul className="list-inline ">
							<li className="list-inline-item text-white"><Link to="/eula" style={{color:"#FFFFFF"}}>Help</Link></li>
						</ul>
					</div>
				</div>
			</footer>
		</div>
 	)

export default AppNg;
