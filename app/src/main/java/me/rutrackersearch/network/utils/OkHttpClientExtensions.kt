package me.rutrackersearch.network.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

inline fun buildRequest(block: Request.Builder.() -> Unit) = Request.Builder().apply(block).build()

private inline fun Request.Builder.url(block: HttpUrl.Builder.() -> Unit) = url(HttpUrl.Builder().apply(block).build())

fun Request.Builder.url(host: String, path: String, vararg query: Pair<String, String?>) = url {
    scheme("https")
    host(host)
    addPathSegment("forum")
    addPathSegment(path)
    apply { query.forEach { (k, v) -> addQueryParameter(k, v) } }
}

inline fun Request.Builder.post(block: FormBody.Builder.() -> Unit) = post(buildFormBody(block))

inline fun buildFormBody(block: FormBody.Builder.() -> Unit) = FormBody.Builder().apply(block).build()

suspend inline fun OkHttpClient.request(block: Request.Builder.() -> Unit) = newCall(buildRequest(block)).await()

fun Response.getString() = this.use { response ->
    checkNotNull(response.body) { "Body must be not null" }.string()
}

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (!continuation.isCancelled) {
                    continuation.resumeWithException(e)
                }
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
        }
    }
}
