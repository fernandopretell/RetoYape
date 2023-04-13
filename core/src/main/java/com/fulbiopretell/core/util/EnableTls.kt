package com.fulbiopretell.retoyape.core.util

import java.security.KeyStore
import java.util.*
import javax.net.ssl.*

object EnableTls {

    fun startTrustManager() {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

        trustManagerFactory.init(null as KeyStore?)

        val trustManagers = trustManagerFactory.trustManagers

        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }

        val trustManager = trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    }

    fun startSocket(): SSLSocketFactory {
        val sc = SSLContext.getInstance("TLSv1.2")
        sc.init(null, null, null)

        return Tls12SocketFactory(sc.socketFactory)
    }

}
