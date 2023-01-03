# HttpRequest

A tiny http client for Kotlin/Android. Only [90 lines](HttpRequest/src/main/kotlin/com/github/mezhevikin/httprequest/HttpRequest.kt) of code. This is wrapper around native HttpURLConnection.

### Get

```kotlin
val request = HttpRequest(
    url = "https://httpbin.org/get",
    parameters = mapOf("name" to "Alex")
)
request.json<HttpBin> { result, response ->
    println(result)
}
```

### Post

```kotlin
val request = HttpRequest(
    url = "https://httpbin.org/post",
    method = Method.POST,
    parameters = mapOf("name" to "Alex"),
    headers = mapOf("User-Agent" to "HttpRequest")
)
request.json<HttpBin> { result, response ->
    println(response.exception)
    println(response.success)
    println(result)
}
```

### Config HttpURLConnection

```kotlin
val request = HttpRequest("https://httpbin.org/get", config = {
    it.readTimeout = 1000
    it.setRequestProperty("User-Agent", "HttpRequest")
})
```

### Json

HttpRequest uses `Kotlinx Serialization` for Json.

```kotlin
@Serializable data class HttpBin(
    val args: Map<String, String>? = null,
    val form: Map<String, String>? = null,
    val headers: Map<String, String>? = null
)

val request = HttpRequest("https://httpbin.org/get")
request.json<HttpBin> { result, response ->
    println(result)
}
```

Custom decoder

```kotlin
HttpRequest.json = Json {
    ignoreUnknownKeys = false
}
```

### String body

```swift
HttpRequest("https://httpbin.org/get").response {
     println(Xml.parse(it.body))
}
```

### Async

HttpRequest makes request and parsing asynchronously.
Use `runOnUiThread` to change UI.

```swift
request.json<Post> { post, response ->
    runOnUiThread {
        titleTextView.text = post.title
    }
}
```

### Install

1. Add JitPack
```
repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

2. Add dependencies

```
implementation 'com.github.mezhevikin:http-request-kotlin:0.0.5'
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1'
```

3. Add plugin

```
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.7.20'
}
```

### Links

🌐 [HttpRequest](https://github.com/mezhevikin/http-request) for Swift/iOS

💹 [Best Currency Converter](https://getconverter.org)

☕️ [Buy me a coffee](https://www.buymeacoffee.com/mezhevikin)
  

