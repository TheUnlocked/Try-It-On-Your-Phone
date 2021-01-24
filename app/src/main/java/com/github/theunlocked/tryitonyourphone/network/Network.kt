package com.github.theunlocked.tryitonyourphone.network

import okhttp3.*
import java.io.IOException

object Network {
    val client = OkHttpClient()

    fun download(url: String, success: (response: Response) -> Unit) = download(url, success, {})
    fun download(url: String, success: (response: Response) -> Unit, failure: (err: IOException) -> Unit) =
        download(Request.Builder()
            .url(url)
            .build(),
            success, failure)

    fun download(request: Request, success: (response: Response) -> Unit) = download(request, success, {})
    fun download(request: Request, success: (response: Response) -> Unit, failure: (err: IOException) -> Unit = {}) =
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = failure(e)
            override fun onResponse(call: Call, response: Response) = success(response)
        })
}