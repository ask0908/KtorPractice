package com.ulternativetechnology.ktorpractice.third

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class PostsServiceImpl(
    private val client: HttpClient
) : PostsService {
    private val TAG = this.javaClass.simpleName

    override suspend fun getPosts(): List<PostResponse> {
        return try {
            client.get {
                url(HttpRoutes.POSTS)
            }
        } catch (e: RedirectResponseException) {
            // 3xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException) {
            // 4xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            emptyList()
        } catch(e: ServerResponseException) {
           // 5xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            emptyList()
         } catch(e: Exception) {
            Log.e(TAG, "Error : ${e.message}")
            emptyList()
          }
    }

    override suspend fun createPost(postRequest: PostRequest): PostResponse? {
        return try {
            client.post<PostResponse> {
                url(HttpRoutes.POSTS)
                contentType(ContentType.Application.Json)
                body = postRequest
            }
        } catch (e: RedirectResponseException) {
            // 3xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            null
        } catch(e: ServerResponseException) {
            // 5xx responses
            Log.e(TAG, "Error : ${e.response.status.description}")
            null
        } catch(e: Exception) {
            Log.e(TAG, "Error : ${e.message}")
            null
        }
    }
}