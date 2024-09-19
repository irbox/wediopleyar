package com.sakura.download.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicInteger
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

fun String.toLongOrDefault(defaultValue: Long): Long {
    return try {
        toLong()
    } catch (_: NumberFormatException) {
        defaultValue
    }
}

fun Long.formatSize(): String {
    require(this >= 0) { "Size must larger than 0." }

    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

    return when {
        tb >= 1 -> "${tb.decimal(2)} TB"
        gb >= 1 -> "${gb.decimal(2)} GB"
        mb >= 1 -> "${mb.decimal(2)} MB"
        kb >= 1 -> "${kb.decimal(2)} KB"
        else -> "${byte.decimal(2)} B"
    }
}

fun Double.decimal(digits: Int): Double {
    return this.toBigDecimal()
        .setScale(digits, RoundingMode.HALF_UP)
        .toDouble()
}

infix fun Long.ratio(bottom: Long): Double {
    if (bottom <= 0) {
        return 0.0
    }
    val result = (this * 100.0).toBigDecimal()
        .divide((bottom * 1.0).toBigDecimal(), 2,  RoundingMode.FLOOR)
    return result.toDouble()
}

suspend fun <T, R> (Collection<T>).parallel(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    max: Int = 2,
    action: suspend CoroutineScope.(T) -> R
): Iterable<R> = coroutineScope {
    val list = this@parallel
    if (list.isEmpty()) return@coroutineScope listOf<R>()

    val channel = Channel<T>()
    val output = Channel<R>()

    val counter = AtomicInteger(0)

    launch {
        list.forEach { channel.send(it) }
        channel.close()
    }

    repeat(max) {
        launch(dispatcher) {
            channel.consumeEach {
                output.send(action(it))
                val completed = counter.incrementAndGet()
                if (completed == list.size) {
                    output.close()
                }
            }
        }
    }

    val results = mutableListOf<R>()
    for (item in output) {
        results.add(item)
    }

    return@coroutineScope results
}

fun ByteArray.decrypt(key: String, iv: String): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val keySpec = SecretKeySpec(key.toByteArray(), "AES")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv.toByteArray()))
    return  cipher.doFinal(this)
}

internal fun createSSLSocketFactory(): SSLSocketFactory {
    return runCatching {
        SSLContext.getInstance("TLS").let {
            it.init(null, arrayOf(TrustAllManager()), SecureRandom())
            it.socketFactory
        }
    }.getOrElse {
        throw it
    }
}

internal class TrustAllManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}

internal class TrustAllCerts : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
}