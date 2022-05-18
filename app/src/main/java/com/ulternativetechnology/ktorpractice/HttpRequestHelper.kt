package com.ulternativetechnology.ktorpractice

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestHelper {
    private val TAG = this.javaClass.simpleName
    // HttpClient(CIO)보다 Android가 더 빨리 데이터를 가져온다. CIO 의존성 삭제함
    private val client: HttpClient = HttpClient(Android) {
        defaultRequest {
//            header("apiHeader", "Bearer ")
            /* install(JsonFeature) {} : response를 코틀린 객체로 직렬화시킴 */
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    private lateinit var response: HttpResponse

    suspend fun test(postId: Int): String = withContext(Dispatchers.IO) {
        response = client.get("https://jsonplaceholder.typicode.com/comments?postId=$postId")
        val responseStatus = response.status
        Log.e(TAG, "responseStatus : $responseStatus")

        if (responseStatus == HttpStatusCode.OK) {
            response.readText()
        } else {
            "error : $responseStatus"
        }
    }
}