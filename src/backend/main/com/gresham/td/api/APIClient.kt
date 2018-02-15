package com.gresham.td.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gresham.td.model.ClientAccount
import com.gresham.td.persistence.ClientAccountRepository
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import org.springframework.beans.factory.annotation.Autowired
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.crypto.RSADecrypter
import com.nimbusds.jose.crypto.RSAEncrypter
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.beans.factory.annotation.Value
import java.io.StringReader
import java.net.URI
import java.util.*
import org.springframework.jdbc.core.JdbcTemplate



class EncryptedPayload(payload : String) {
	val encryptedPayload = payload
}

class AccountBalanceRequest(val accountHolderRef: String, val currency: String = "AUD", val accountType: String = "CLI") {

}

class AccountBalanceResponse() {
	val bankBalance: Double = 0.0
	val bankBalanceDate: Date = Date()
	val forwardBalance: Double = 0.0
	val forwardBalanceDate: Date = Date()
	val ledgerBalance: Double = 0.0
	val ledgerBalanceDate: Date = Date()
}


class APIClient(val cryptoManager: CustomerCryptoManager, val locationCode: String) {
	@Autowired
	lateinit var clientAccountRepository: ClientAccountRepository

	@Value("\${api_base_uri}")
	private val apiBase: String? = null

	/* Actually reading the account list from the DB, which is faster and there's no API currently supporting that anyway */
	fun accountList(currency: String = "AUD", accountType: String = "CLI"): Iterable<ClientAccount> {
		return clientAccountRepository.clientAccounts(locationCode, accountType)
	}

	fun accountBalance(accountHolderRef: String, currency: String = "AUD", accountType: String = "CLI"): AccountBalanceResponse {
		val request = AccountBalanceRequest(accountHolderRef, currency, accountType)
		val payload = encryptPayload(request)
		val client = cryptoManager.APISocket()
		val post = HttpPost(URI.create(apiBase + "balances/enquiry"))
		post.setHeader("content-type", "application/json")
		post.entity = StringEntity(payload)
		val response = client.execute(post)
		val sr = IOUtils.toString(response.entity.content)
		val balance: AccountBalanceResponse = decryptPayload(sr)
		return balance
	}

	//TODO
	// Creates a term deposit account
	fun createAccount(accountHolderRef: String, description: String, currency: String = "AUD", type: String = "CTD" ):ClientAccount {
		val clientAccount: ClientAccount = ClientAccount()
		clientAccount.id = accountHolderRef
		clientAccount.currency = currency
		clientAccount.name = description
		clientAccount.type = type
		return clientAccount
	}

	//TODO
	fun createTransaction() {

	}

	//TODO
	fun closeAccount(accountHolderRef: String) {

	}

	private fun <T> encryptPayload(payload: T) : String {
		val rsaKey = cryptoManager.RSAkey()
		val mapper = ObjectMapper();
		val jsonString = mapper.writeValueAsString(payload)

		val signer = RSASSASigner(rsaKey)

		val jwsObject = JWSObject(
				JWSHeader.Builder(JWSAlgorithm.RS256)
						.contentType("JWS") // required to signal nested JWS
						.build(),
				Payload(jsonString))

		// sign the payload
		jwsObject.sign(signer)

		// encrypt the payload
		val header = JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A256CBC_HS512)
		val jweObject = JWEObject(header, Payload(jwsObject))
		jweObject.encrypt(RSAEncrypter(rsaKey))

		// Output to URL-safe format
		return mapper.writeValueAsString(EncryptedPayload(jwsObject.serialize()))
	}

	private inline fun <reified T> decryptPayload(payload: String) : T {
		// payload is a JSON object: {encryptedPayload: <JWE> }
		val rsaKey = cryptoManager.RSAkey()

		val mapper = ObjectMapper()
		val encryptedPayload: EncryptedPayload = mapper.readValue(payload)

		// decrypt the payload
		val decrypter = RSADecrypter(rsaKey)
		val jweObject = JWEObject.parse(encryptedPayload.encryptedPayload)
		jweObject.decrypt(decrypter)
		val signedJWT = jweObject.payload.toSignedJWT()

		//TODO check the signature
		signedJWT.payload

		val result: T = mapper.readValue(signedJWT.payload.toString(), T::class.java)
		return result
	}

	fun post(URI: String, payload: Any) : String {
		val encryptedPayload = encryptPayload(payload)
		return ""
	}
}
