package com.github.mezhevikin.httprequest

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.net.SocketTimeoutException
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class HttpRequestInstrumentedTest {
    @Test fun testGetRequest() {
        val latch = CountDownLatch(1)
        val request = HttpRequest(
            url = "https://httpbin.org/get",
            method = Method.GET,
            parameters = mapOf(
                "firstName" to "Alexey",
                "lastName" to "Mezhevikin"
            ),
            headers = mapOf(
                "User-Agent" to "HttpRequest"
            )
        )
        request.json<HttpBin> { result, response ->
            assertNotNull(response.body)
            assertNull(response.exception)
            assertNotNull(result)
            assertEquals(200, response.connection?.responseCode)
            assertTrue(response.success)
            assertEquals(result?.args?.get("firstName"), "Alexey")
            assertEquals(result?.args?.get("lastName"), "Mezhevikin")
            assertEquals(result?.headers?.get("User-Agent"), "HttpRequest")
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testPostRequest() {
        val latch = CountDownLatch(1)
        val request = HttpRequest(
            url = "https://httpbin.org/post",
            method = Method.POST,
            parameters = mapOf(
                "firstName" to "Alexey",
                "lastName" to "Mezhevikin"
            ),
            headers = mapOf(
                "User-Agent" to "HttpRequest"
            )
        )
        request.json<HttpBin> { result, response ->
            assertNotNull(response.body)
            assertNull(response.exception)
            assertNotNull(result)
            assertEquals(200, response.connection?.responseCode)
            assertTrue(response.success)
            assertEquals(result?.form?.get("firstName"), "Alexey")
            assertEquals(result?.form?.get("lastName"), "Mezhevikin")
            assertEquals(result?.headers?.get("User-Agent"), "HttpRequest")
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testHeadRequest() {
        val latch = CountDownLatch(1)
        HttpRequest("https://httpbin.org/headers", Method.HEAD).response { response ->
            assertEquals(200, response.connection?.responseCode)
            assertEquals(response.body, "")
            assertNull(response.exception)
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testNotFound() {
        val latch = CountDownLatch(1)
        HttpRequest("https://httpbin.org/status/404").response { response ->
            assertFalse(response.success)
            assertNotNull(response.exception)
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testRequestTimeout() {
        val latch = CountDownLatch(1)
        val request = HttpRequest("https://httpbin.org/delay/10", config = {
            it.readTimeout = 1000
        })
        request.response { response ->
            assertNull(response.body)
            assertNotNull(response.exception)
            assertTrue(response.exception is SocketTimeoutException)
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testJsonError() {
        val latch = CountDownLatch(1)
        val request = HttpRequest("https://httpbin.org/get")
        val decoder = Json { ignoreUnknownKeys = false }
        request.json<HttpBin>(decoder) { result, response ->
            assertNotNull(response.exception)
            assertNull(result)
            latch.countDown()
        }
        latch.await()
    }

    @Test fun testQueryEncoding() {
        assertEquals(
            "username=mezhevikin&password=%21%40%23%24%25%5E%26*%28%29",
            mapOf(
                "username" to "mezhevikin",
                "password" to "!@#$%^&*()"
            ).query
        )
    }
}

@Serializable
data class HttpBin(
    val args: Map<String, String>? = null,
    val form: Map<String, String>? = null,
    val headers: Map<String, String>? = null
)