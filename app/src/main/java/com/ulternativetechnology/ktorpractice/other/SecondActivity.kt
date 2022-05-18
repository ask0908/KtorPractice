package com.ulternativetechnology.ktorpractice.other

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.ulternativetechnology.ktorpractice.R
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/* https://www.youtube.com/watch?v=goJGH77zjag */
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        KtorWrapper.onLog = {
            Log.e("SecondActivity", "onLog : $it")
        }

        findViewById<Button>(R.id.buttonGet).setOnClickListener {
            get()
        }

        findViewById<Button>(R.id.buttonPost).setOnClickListener {
            post()
        }
    }

    private fun get() {
        lifecycleScope.launch {
            try {
                coroutineScope {
                    val requestToken = async(Dispatchers.IO) {
                        KtorWrapper.get(
                            "https://jsonplaceholder.typicode.com/comments?postId=1",
                            queryParameterMap = mutableMapOf<String, Any>().apply {
                                put("postId", 1)
                            }
                        )
                    }

                    val requestTokenResponse = requestToken.await()

                    val jsonArray = JSONArray(requestTokenResponse.readText())

//                    val body = jsonArray.getString("body")
                    val infoRequest = async(Dispatchers.IO) {
                        KtorWrapper.get(
                            "https://jsonplaceholder.typicode.com/comments?postId=1",
                            headerMap = mutableMapOf<String, String>().apply {
                                put("Content-Type", "application/json")
                            }
                        )
                    }

                    val infoResponse = infoRequest.await()
                    Log.e("SecondActivity", "response : ${infoResponse.readText()}")
                }
            } catch (e: Exception) {
                Log.e("SecondActivity", "error : ${e.message}")
            }
        }
    }

    private fun post() {
        //
    }

}