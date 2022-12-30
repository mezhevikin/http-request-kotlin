# HttpRequest

A tiny http client for Kotlin/Android. Only [90 lines](HttpRequest/src/main/kotlin/com/github/mezhevikin/httprequest/HttpRequest.kt) of code. This is wrappe around native HttpURLConnection.

### Get

```kotlin
val request = HttpRequest(
    url = "https://httpbin.org/get",
    parameters = mapOf("name" to "Alex"),
)
request.json<HttpBin> { json, response ->
    println(json)
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
request.json<HttpBin> { json, response ->
    println(response.error)
    println(response.success)
    println(json)
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

HttpRequest use `Kotlinx Serialization` for Json.

```kotlin
@Serializable data class HttpBin(
    val args: Map<String, String>? = null,
    val form: Map<String, String>? = null,
    val headers: Map<String, String>? = null
)

val request = HttpRequest("https://httpbin.org/get")
request.json<HttpBin> { json, response ->
    println(json)
}
```

Custom decoder

```kotlin
val request = HttpRequest("https://httpbin.org/get")
val decoder = Json { ignoreUnknownKeys = false }
request.json<HttpBin>(decoder) { json, response ->
    println(json)
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
implementation 'com.github.mezhevikin:http-request-kotlin:0.0.2'
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
  

