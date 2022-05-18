package com.ulternativetechnology.ktorpractice.third

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ulternativetechnology.ktorpractice.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/* https://www.youtube.com/watch?v=3KTXD_ckAX0
* 영상에선 Compose를 기반으로 사용해서 액티비티에서 사용하는 코드는 내가 만듬 */
class ThirdActivity : AppCompatActivity(), CoroutineScope {

    private val TAG = this.javaClass.simpleName
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val client = PostsService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_third)

        // suspend fun을 썼기 때문에 코루틴을 써야 한다
        CoroutineScope(Dispatchers.IO).launch {
            val response: List<PostResponse> = client.getPosts()
            for (i in response.indices) {
                Log.e(TAG, "response[i].id : ${response[i].id}")
                Log.e(TAG, "response[i].body : ${response[i].body}")
                Log.e(TAG, "response[i].title : ${response[i].title}")
                Log.e(TAG, "response[i].userId : ${response[i].userId}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()    // 액티비티 종료 시 job 종료
    }

}