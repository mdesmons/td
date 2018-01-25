import React from 'react'

const Alert = ({error, onClearError}) => {
	if (error) {
  		const componentClasses = ['alert', 'alert-danger', 'm-3', 'fade'];

		if (error.get('active')) {
		  componentClasses.push('show');
		} else {
		  componentClasses.push('d-none');
		}

		return (
			<div className={componentClasses.join(' ')} role="alert">
			  {error.get('message')}
			  <button type="button" className="close" aria-label="Close" onClick={() => onClearError()}>
                  <span aria-hidden="true">&times;</span>
                </button>
			</div>
		)
	}

	return (<div></div>)
}


export default Alert
