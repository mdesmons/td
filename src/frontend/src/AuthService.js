import decode from 'jwt-decode';
import { browserHistory } from 'react-router';
import auth0 from 'auth0-js';
var config = require('config')
const ID_TOKEN_KEY = 'id_token';
const ACCESS_TOKEN_KEY = 'access_token';

const CLIENT_ID = 'iarob3YCLCJcAt48kRCJCrEiJicV242C';
const CLIENT_DOMAIN = 'tennisladder.au.auth0.com';
const SCOPE = 'standard openid email offline_access';
const AUDIENCE = 'https://www.tennisladder.fun';

export function logout() {
  clearAccessToken();
}

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}


function clearAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}


function getTokenExpirationDate(encodedToken) {
  const token = decode(encodedToken);
  if (!token.exp) { return null; }

  const date = new Date(0);
  date.setUTCSeconds(token.exp);

  return date;
}

function isTokenExpired(token) {
  const expirationDate = getTokenExpirationDate(token);
  console.log("Token expiration date: " + expirationDate)
  return expirationDate < new Date();
}

function getConnectionStatus(encodedToken) {
    const token = decode(encodedToken);
    // Get the time that the access token will expire at
    const expiresAt = getTokenExpirationDate(encodedToken);
    const scope = token.scope || '';

    /* Return an object that will be used to update the model */
    return {
    	expiration: expiresAt,
    	scope: scope
   	}
}

export function setSession(encodedToken) {
     localStorage.setItem('access_token', encodedToken);

    /* Return an object that will be used to update the model */
     return {
     connectionStatus: {
     ...getConnectionStatus(encodedToken),
     logged: true
     }}
}

export function populateConnectionStatus() {
	const token = localStorage.getItem(ACCESS_TOKEN_KEY);
	if (token == null || isTokenExpired(token)) {
	    return {
         connectionStatus: {
	         logged: false
         }}
	} else {
		 return {
		 connectionStatus: {
		 ...getConnectionStatus(token),
		 logged: true
		 }}
	}
}
