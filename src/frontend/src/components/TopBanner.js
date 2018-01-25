import React from 'react'
import { Link } from 'react-router'

const TopBanner = ({message}) =>  (
	<div className="container-fluid">
		<div className="alert alert-warning my-2">
			{message}
		</div>
	</div>
)

export default TopBanner;
