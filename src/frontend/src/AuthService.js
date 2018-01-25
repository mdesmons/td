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


var auth = new auth0.WebAuth({
  clientID: CLIENT_ID,
  domain: CLIENT_DOMAIN
});

export function login() {
  auth.authorize({
    responseType: 'token id_token',
    redirectUri: config.redirect,
    audience: AUDIENCE,
    scope: SCOPE
  });
}

export function logout() {
  clearIdToken();
  clearAccessToken();
}

export function getIdToken() {
  return localStorage.getItem(ID_TOKEN_KEY);
}

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

function clearIdToken() {
  localStorage.removeItem(ID_TOKEN_KEY);
}

function clearAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

// Helper function that will allow us to extract the access_token and id_token
function getParameterByName(name) {
  let match = RegExp('[#&]' + name + '=([^&]*)').exec(window.location.hash);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

// Get and store access_token in local storage
export function setAccessToken(accessToken) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
}

// Get and store id_token in local storage
export function setIdToken() {
  let idToken = getParameterByName('id_token');
  localStorage.setItem(ID_TOKEN_KEY, idToken);
}

export function isLoggedIn() {
  const accessToken = getAccessToken();
  return !!accessToken && !isTokenExpired(accessToken);
}

export function isAdmin() {
	if (localStorage.getItem('scopes') == null) {
	return false
	}

	return JSON.parse(localStorage.getItem('scopes')).includes("desk")
}

export function hasScope(scope) {
	if (localStorage.getItem('scopes') == null) {
	return false
	}

	return JSON.parse(localStorage.getItem('scopes')).includes(scope)
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

export function setSession(encodedToken) {
    const token = decode(encodedToken);
	console.log(token)
    // Set the time that the access token will expire at
    const expiresAt = getTokenExpirationDate(encodedToken);
    const scopes = token.scope || '';

    localStorage.setItem('access_token', encodedToken);
    localStorage.setItem('expires_at', expiresAt);
    localStorage.setItem('scopes', JSON.stringify(scopes.split(' ')));
  }

 export function handleAuthentication(callback) {
    auth.parseHash({hash: window.location.hash}, (err, authResult) => {
    	if (authResult && authResult.accessToken && authResult.idToken) {
        	window.location.hash = '';
        	setSession(authResult);
        	if (callback) { callback(null) }
      	} else if (err) {
			console.log(err);
			alert('Error: ${err.error}. Check the console for further details.');
			callback(err)
      } else {
        	if (callback) { callback(null) }
      }
    });
  }

export function refresh(callback) {
 auth.checkSession({
    responseType: 'token id_token',
    redirectUri: config.redirect,
    audience: AUDIENCE,
    scope: SCOPE
  }, callback);
}
