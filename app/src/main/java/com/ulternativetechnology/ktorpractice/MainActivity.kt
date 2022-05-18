package com.ulternativetechnology.ktorpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val TAG = this.javaClass.simpleName
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_main)

        test()
    }

    private fun test() {
        launch(Dispatchers.Main) {
            val result = HttpRequestHelper().test(1)
            Log.e(TAG, "result : $result")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()    // 액티비티 종료 시 job 종료
    }

}