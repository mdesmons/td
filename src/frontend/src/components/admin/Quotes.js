import React from 'react'
import Alert from '../Alert'

class AddQuote extends React.Component {
  constructor(props) {
	super(props)
	this.state =  {
		rate: 0.0
	}

    this.onRateChange = this.onRateChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

  	onSubmit(event) {
  		event.preventDefault()
  		if (event.target.checkValidity()) {
  			var output = {
  				rate: this.state.rate
  			}
  			this.props.onSubmit(output)
  		} else {
  			event.target.classList.add('was-validated');
        }
  	}

  	onRateChange(event) {
  		this.setState({rate : event.target.value})
  	}

  		render() {

    	return (
    	<div className="container-fluid">
    		<Alert error={this.props.error} onClearError={this.props.onClearError} />
    		<div className="row">
    			<div className="col-md-1"></div>
    			<div className="col-md-10">
    				<div className="bg-light rounded p-3">
    					<h2 className="display-4">Create Quote</h2>
    					<p>This form lets you create a quote for a Term Deposit. All quotes expire at 3pm the same day.</p>
    					<form className="my-3" onSubmit={this.onSubmit} noValidate>
    						<div className="form-group">
    							<label htmlFor="rate">Proposed Interest rate</label>
    							<input type="number" step="0.01" min="0" className="form-control" id="rate" value= {this.state.rate} onChange={this.onRateChange}/>
    						</div>
    						<button type="submit" className="btn btn-success mr-3">Create Quote</button>
    					</form>
    				</div>
    			</div>
    		</div>
    	</div>
    )}
}

const QuoteItem = ({quote, onClick}) => (
		<tr  onClick={onClick}>
		<td>{(new Date(quote.get('openingDate'))).toLocaleString()}</td>
		<td>{quote.get('reference')}</td>
		<td>{quote.get('rate') + '%'}</td>
		<td><button onClick={onClick}>close</button></td>
		</tr>
)

class QuoteList extends React.Component {
  constructor(props) {
	super(props)
	this.state = {
		asc: true,
		sortField: 'reference'
	}

    this.getQuoteList = this.getQuoteList.bind(this);
    this.swapOrder = this.swapOrder.bind(this);
    this.onHeaderClick = this.onHeaderClick.bind(this);
    this.sortIcon = this.sortIcon.bind(this);
  }

	onHeaderClick(field) {
		if (this.state.sortField == field) {
			this.swapOrder()
		} else {
			this.setState({sortField: field})
			this.forceUpdate()
		}
	}

	swapOrder() {
		this.setState({asc : !this.state.asc})
		this.forceUpdate()
	}

	sortIcon(field) {
		if (field == this.state.sortField) {
			if (this.state.asc) {
				return "fa fa-sort-up"
			} else {
				return "fa fa-sort-down"
			}
		} else {
			return "fa fa-sort"
		}
	}


	getQuoteList(data) {
		var self = this
		if (this.state.asc) {
			return data.sort(function(a, b) {
				if (a.get(self.state.sortField) == b.get(self.state.sortField)) return 0
				if (a.get(self.state.sortField) < b.get(self.state.sortField)) return -1
				if (a.get(self.state.sortField) > b.get(self.state.sortField)) return 1
				})
		} else {
			return data.sort(function(a, b) {
				if (a.get(self.state.sortField) == b.get(self.state.sortField)) return 0
				if (a.get(self.state.sortField) > b.get(self.state.sortField)) return -1
				if (a.get(self.state.sortField) < b.get(self.state.sortField)) return 1
				})
		}
	}

	componentDidUpdate(prevProps, prevState, snapshot) {
		console.log("Component updated")
	}

  render() {
//							{this.getQuoteList().map(item => <QuoteItem key={item.get('id')} quote={item} onClick={() => this.props.onItemSelected(item.get('id'))} />)}
	return (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<h1 className="display-4">Current Quotes</h1>
					<table className="table table-hover">
						<thead>
							<tr>
								<th scope="col" onClick = {() => this.onHeaderClick('openingDate')}>Created&nbsp;<i className={this.sortIcon('openingDate')} aria-hidden="true"></i> </th>
								<th scope="col" onClick = {() => this.onHeaderClick('reference')}>Reference&nbsp;<i className={this.sortIcon('reference')} aria-hidden="true"></i> </th>
								<th scope="col" onClick = {() => this.onHeaderClick('rate')}>Rate&nbsp;<i className={this.sortIcon('rate')} aria-hidden="true"></i> </th>
								<th>Close</th>
							</tr>
						</thead>
						<tbody>
							{this.getQuoteList(this.props.quotes).map(item => <QuoteItem key={item.get('id')} quote={item} onClick={() => {this.props.onCloseQuote(item.get('id'))}}/>)}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	)
	}
}

const Quotes = ({onError, onClearError, onSubmit, onCloseQuote, quotes}) => (
	<div className="container-fluid">
		<div className="row">
			<div className="col-md-1"></div>
			<div className="col-md-10">
				<div className="bg-light rounded p-3">
					<AddQuote onSubmit={onSubmit} />
					<QuoteList quotes={quotes} onCloseQuote={onCloseQuote} />
				</div>
			</div>
		</div>
	</div>
)


export default Quotes;
