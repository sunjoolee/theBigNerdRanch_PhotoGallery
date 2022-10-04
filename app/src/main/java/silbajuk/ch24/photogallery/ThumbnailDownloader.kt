package silbajuk.ch24.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.NonCancellable.start
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"

//메세지 객체의 what 속성으로 설정할 상수
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(
    //main스레드로부터 전달된 Handler의 참조를 갖는 속성
    private val responseHandler: Handler,
    //응답 측(내려받은 이미지)와 요청 측(main 스레드)간의 소통을 위한 콜백에 사용될 함수 타입 속성
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG), DefaultLifecycleObserver {
    private var hasQuit = false

    //모든 Retrofit 설정 코드는 스레드의 생이 동안 한번만 실행된다
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetchr = FLickrFetchr()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        //HandlerThread.onLooperPrepared()는 Looper가 최초로 큐를 확인하기 전에 호출됨
        requestHandler = object : Handler(){
            override fun handleMessage(msg: Message) {
                //내려받기 요청 메세지를 큐에서 꺼내어 처리할 준비가 되먄 이 함수가 호출됨
                val target = msg.obj as T
                Log.i(TAG, "Got a request fot URL: ${requestMap[target]}")
                handleRequest(target)

            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.i(TAG, "Starting background Thread")
        start()
        looper
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.i(TAG, "Destroying background Thread")
        quit()
    }

    //이 함수는 PhotoGalleryFragment.kt에서 PhotoAdapter의 onBindViewHolder(...)가 호출
    fun queueThumbnail(target : T, url:String){
        Log.i(TAG, "Got a URL : $url")

        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)
            .sendToTarget()
    }

    private fun handleRequest(target: T){
        val url = requestMap[target]?:return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return

        //이미지를 내려받아 보여주기
        responseHandler.post(Runnable{
            if(requestMap[target] != url || hasQuit){
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnailDownloaded(target,bitmap)
        })
    }
}