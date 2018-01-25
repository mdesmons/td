import React from 'react'
import {AdminLadderListContainer} from '../../containers/LadderListContainer'
import PlayerListContainer from '../../containers/PlayerListContainer'
import PlayerList from './PlayerList'
import PendingSubscriptionListContainer from '../../containers/PendingSubscriptionListContainer'

class AdminHome extends React.Component {

  constructor() {
    super()
  }

  render() {
	return (
		<div className="container-fluid">
			<div className="row">
				<h1 className="col-sm offset-sm-1">Welcome, Admin</h1>
			</div>
			<div className="row">
				<div className="col-md-4">
					<AdminLadderListContainer/>
				</div>
				<div className="col-md-4">
					<PlayerListContainer/>
				</div>
				<div className="col-md-4">
					<PendingSubscriptionListContainer/>
				</div>
			</div>
        </div>
        )
    }
}

export default AdminHome;
