package com.ulternativetechnology.ktorpractice.other

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File

data class FormDataFile(
    val fileKey: String,        // Ask your server developer to get this
    val filePath: String,
    val fileName: String
)

object KtorWrapper {
    private var client: HttpClient? = null

    var onLog: ((message: String) -> Unit)? = null

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(Android) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        onLog?.let {
                            it(message)
                        }
                    }
                }
                level = LogLevel.ALL
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
                acceptContentTypes = acceptContentTypes + ContentType.Application.Json
            }

            engine {
                connectTimeout = 100_000
                socketTimeout = 100_000
            }

            HttpResponseValidator {
                handleResponseException { e ->
                    onLog?.let { onLog ->
                        e.message?.let { message ->
                            onLog(message)
                        }
                    }
                }
            }
        }
    }

    /**
     * Put in onDestroy() of your main activity if you need
     */
    fun releaseClient() {
        client?.close()
        client = null
    }

    suspend fun get(
        url: String,
        headerMap: MutableMap<String, String>? = null,
        queryParameterMap: MutableMap<String, Any>? = null
    ): HttpResponse {
        var urlString = url
        queryParameterMap?.let {
            var queryParameterString = "?"
            for ((key, value) in it) {
                queryParameterString += "$key=$value$"
            }

            // Drop last '&'
            urlString += queryParameterString.dropLast(1)
        }

        if (client == null) {
            createClient()
        }

        return client!!.request {
            this.method = HttpMethod.Get
            this.url(urlString)
            this.contentType(ContentType.Application.Json)

            // If header
            headerMap?.let {
                for ((key, value) in it) {
                    this.header(key, value)
                }
            }
        }

    }

    suspend fun post(
        url: String,
        headerMap: MutableMap<String, String>? = null,
        formDataMap: MutableMap<String, String>? = null,
        formDataFileList: List<FormDataFile>? = null
    ) {
        if (client == null) {
            createClient()
        }

        return client!!.request {
            this.method = HttpMethod.Post
            this.url(url)

            headerMap?.let {
                for ((key, value) in it) {
                    this.header(key, value)
                }
            }

            this.body = MultiPartFormDataContent(
                formData {
                    // Form data
                    formDataMap?.let {
                        for ((key, value) in it) {
                            this.append(key, value)
                        }
                    }

                    // Form data file
                    formDataFileList?.let {
                        for (formDataFile in it) {
                            val file = File(formDataFile.filePath)
                            this.append(formDataFile.fileKey, file.readBytes(), Headers.build {
                                // In this case, I only post image file, you can change this to whatever you want
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=${formDataFile.fileName}")
                            })
                        }
                    }
                }
            )
        }
    }

}
