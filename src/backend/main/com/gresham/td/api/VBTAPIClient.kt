package com.gresham.td.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gresham.td.api.dto.AccountBalanceRequest
import com.gresham.td.api.dto.AccountBalanceResponse
import com.gresham.td.model.ClientAccount
import com.gresham.td.model.Customer
import com.gresham.td.persistence.ClientAccountRepository
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import org.springframework.beans.factory.annotation.Autowired
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.crypto.RSADecrypter
import com.nimbusds.jose.crypto.RSAEncrypter
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.File
import java.net.URI



@Component
@Configuration
@Profile("!dev")
class VBTAPIClient() : APIClient {
	@Autowired
	lateinit var clientAccountRepository: ClientAccountRepository

	@Value("\${api_base_uri}")
	private val apiBase: String? = null

	@Value("\${certroot}")
	private val certRoot: String? = null

	private var cryptoManager: CustomerCryptoManager? = null

	private var locationCode: String = ""

	class EncryptedPayload(payload : String) {
		val encryptedPayload = payload
	}

	override fun setCustomer(customer: Customer) : APIClient {
		cryptoManager = CustomerCryptoManager(certRoot + File.separator + customer.certificate, customer.keystorePass, customer.keyAlias)
		locationCode = customer.locationCode
		return this
	}

	/* Actually reading the account list from the DB, which is faster and there's no API currently supporting that anyway */
	override fun accountList(currency: String, accountType: String): Iterable<ClientAccount> {
		return clientAccountRepository.clientAccounts(locationCode, accountType)
	}

	override fun accountBalance(accountHolderRef: String, currency: String, accountType: String): AccountBalanceResponse {
		val request = AccountBalanceRequest(accountHolderRef, currency, accountType)
		val response = post("balances/enquiry", request)
		val sr = IOUtils.toString(response?.entity?.content, "UTF-8")
		val balance: AccountBalanceResponse = decryptPayload(sr)
		return balance
	}

	//TODO
	// Creates a term deposit account
	override fun createAccount(accountHolderRef: String, description: String, currency: String, type: String ):ClientAccount {
		val clientAccount: ClientAccount = ClientAccount()
		clientAccount.id = accountHolderRef
		clientAccount.currency = currency
		clientAccount.name = description
		clientAccount.type = type
		return clientAccount
	}

	//TODO
	override fun createTransaction() {

	}

	//TODO
	override fun closeAccount(accountHolderRef: String) {

	}

	private fun <T> encryptPayload(payload: T) : String {
		val rsaKey = cryptoManager?.RSAkey()
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
		val rsaKey = cryptoManager?.RSAkey()

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

	fun post(resource: String, payload: Any) : CloseableHttpResponse? {
		val client = cryptoManager?.APISocket()
		val post = HttpPost(URI.create(apiBase + resource))
		post.setHeader("content-type", "application/json")
		post.entity = StringEntity(encryptPayload(payload))
		return client?.execute(post)
	}
}
