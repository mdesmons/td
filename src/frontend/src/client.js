'use strict';

import jquery from 'jquery'
import { normalize, schema } from 'normalizr';
import { fromJS, Map } from 'immutable';
import { getAccessToken } from './AuthService';
import { apiBase}  from 'config'

export function restGet(url, requestHeaders) {
	console.log("API call: " + url)

	return jquery.ajax(
		{type: "GET",
		 url: apiBase + url,
		 headers: { "Authorization": "Bearer " + getAccessToken(),
		 "Content-Type": "application/hal+json",
		 ...requestHeaders
		 }
	})
}

export function restPut(url, payload) {
	console.log("API call: " + url)
	return jquery.ajax(
				{type: "PUT",
				 url: apiBase + url,
				 data: JSON.stringify(payload),
				 headers: { "Authorization": "Bearer " + getAccessToken(),
				 "Content-Type": "application/hal+json"}
				 })
}

export function restPost(url, payload, anon = false){
	console.log("API call: " + url)
	var data = {type: "POST",
				 url: apiBase + url,
				 data: JSON.stringify(payload),
				 headers: { "Content-Type": "application/hal+json"}
				 }
	if (!anon) {
		data.headers["Authorization"] = "Bearer " + getAccessToken()
	}

	return jquery.ajax(data)
}

export function restDelete(url, payload) {
	console.log("API call: " + url)
	return jquery.ajax(
		{type: "DELETE",
		 url: apiBase + url,
		data: JSON.stringify(payload),
		 headers: { "Authorization": "Bearer " + getAccessToken(),
		 "Content-Type": "application/hal+json"}
	 })
}

export function normalizeComplexResponse(originalData) {
	console.log("Original data: ")
	console.log(originalData)

		// Define the schema
	const transferSchema = new schema.Entity('transfers');
	const transferListSchema = new schema.Array(transferSchema);

	const termDepositSchema = new schema.Entity('termDeposits', {transfers: transferListSchema});
	const termDepositListSchema = new schema.Array(termDepositSchema);
	const clientAccountSchema = new schema.Entity('clientAccounts', {clientAccounts: clientAccountListSchema});
	const clientAccountListSchema = new schema.Array(clientAccountSchema);

	const quoteSchema = new schema.Entity('quotes');
	const quoteListSchema = new schema.Array(quoteSchema);

	const customerSchema = new schema.Entity('customers', {termDeposits: termDepositListSchema,
	clientAccounts: clientAccountListSchema,
	quotes: quoteListSchema}, {idAttribute: "locationCode"});
	const customerListSchema = new schema.Array(customerSchema);

	const interestRateSchema = new schema.Entity('interestRate');

	const errorSchema = new schema.Entity('error');
	const connectionStatusSchema = new schema.Entity('connectionStatus');


	const modelSchema = {
		customers: [customerSchema],
		clientAccounts: [clientAccountSchema],
		transfers: [transferSchema],
		termDeposits: [termDepositSchema],
		interestRate: [interestRateSchema],
		quotes: [quoteSchema]
	};

	let normalizedModel = normalize(originalData, modelSchema).entities;



	console.log("Normalised data: ")
	console.log(normalizedModel)

	normalizedModel.error = originalData.error
	normalizedModel.connectionStatus = originalData.connectionStatus

	let model = fromJS(normalizedModel);

	if (!model.has('customers')) { model = model.set('customers', Map())}
	if (!model.has('termDeposits')) { model = model.set('termDeposits', Map())}
	if (!model.has('transfers')) { model = model.set('transfers', Map())}
	if (!model.has('clientAccounts')) { model = model.set('clientAccounts', Map())}
	if (!model.has('quotes')) { model = model.set('quotes', Map())}

	return model
}
