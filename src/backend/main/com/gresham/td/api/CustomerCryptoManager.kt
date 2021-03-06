package com.gresham.td.api

import com.nimbusds.jose.jwk.RSAKey
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

class CustomerCryptoManager(val certPath: String = "", val keystorePass: String = "", val alias: String = "") {

	fun RSAkey(): RSAKey {
		return RSAKey.load(keyStore(), alias, keystorePass.toCharArray())
	}

	fun keyStore(): KeyStore {
		val keyStore = KeyStore.getInstance("PKCS12");
		val certInputStream = FileInputStream(certPath);
		keyStore.load(certInputStream, keystorePass.toCharArray())
		return keyStore
	}

	fun APISocket() : CloseableHttpClient {
		// Trust own CA and all self-signed certs
		val sslcontext = SSLContexts.custom()
				.setProtocol("TLSv1.2")
				.loadKeyMaterial(keyStore(), keystorePass.toCharArray())
				//	.loadTrustMaterial(keyStore, TrustSelfSignedStrategy())
				.build()

		var httpclient = HttpClients.custom()
				.setSSLSocketFactory(SSLConnectionSocketFactory(sslcontext))
				.build()

		return httpclient
	}
}
