package com.github.mezhevikin.httprequest

import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Executors
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class HttpRequest(
    val url: String,
    val method: Method = Method.GET,
    val parameters: Map<String, Any> = mapOf(),
    val headers: Map<String, String> = mapOf(),
    val config: ((HttpURLConnection) -> Unit)? = null
) {
    fun response(completion: (HttpResponse) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val url = if (method == Method.GET && parameters.isNotEmpty()) {
                    URL("$url?${parameters.query}")
                } else {
                    URL(url)
                }
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method.value
                headers.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }
                config?.let { it(connection) }
                if (method == Method.POST && parameters.isNotEmpty()) {
                    connection.doOutput = true
                    connection.outputStream.use {
                        it.write(parameters.query.toByteArray())
                    }
                }
                val response = HttpResponse()
                response.connection = connection
                response.body = connection.inputStream.use {
                    BufferedReader(InputStreamReader(it)).use { reader ->
                        reader.readText()
                    }
                }
                completion(response)
            } catch (e: Exception) {
                val response = HttpResponse()
                response.exception = e
                completion(response)
            }
        }
    }

    companion object {
        var json = Json { ignoreUnknownKeys = true }
    }

    inline fun <reified T> json(crossinline completion: (T?, HttpResponse) -> Unit) where T : Any {
        response { response ->
            try {
                val body = response.body
                val result = if (body != null) json.decodeFromString<T>(body) else null
                completion(result, response)
            } catch (e: Exception) {
                response.exception = e
                completion(null, response)
            }
        }
    }
}

enum class Method(val value: String) {
    GET("GET"), HEAD("HEAD"), POST("POST"), PUT("PUT"),
    DELETE("DELETE"), OPTIONS("OPTIONS"), TRACE("TRACE"), PATCH("PATCH")
}

class HttpResponse {
    var connection: HttpURLConnection? = null
    var body: String? = null
    var exception: Exception? = null
    val success: Boolean get() {
        connection?.let { return it.responseCode in 200..299 }
        return false
    }
}

val Map<String, Any>.query: String get() {
    return this.map { (key, value) -> "$key=${value.urlEncoded}" }.joinToString("&")
}

val Any.urlEncoded: String get() = URLEncoder.encode(toString(), "UTF-8")