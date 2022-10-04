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
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG), DefaultLifecycleObserver {
    private var hasQuit = false

    //모든 Retrofit 설정 코드는 스레드의 생이 동안 한번만 실행된다
    private lateinit var requestHandler: Handler

    //ConcurrentHashMap: 스레드에 안전한 HashMap
    //내려받기 요청의 식별 개체(PhotoHolder)를 키로 사용하여 특정 다운로드 요청과 연관된 URL을 저장하고 꺼냄
    private val requestMap = ConcurrentHashMap<T, String>()

    private val flickrFetchr = FLickrFetchr()

    //메세지 처리하기
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

    //생명주기 소유자의 onCreate(...)와 onDestroy() 함수들을 관찰
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun setup(){
//        Log.i(TAG, "Starting background Thread")
//    }
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun tearDown(){
//        Log.i(TAG, "Destroying background Thread")
//    }
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
    //이때 내려받을 이미지가 위치하는 PhotoHolder와 이미지를 내려받기 위한 URL을 함수 인자로 전달
    //target: 내려받기의 식별자로 사용하기 위한 T 타입 객체
    fun queueThumbnail(target : T, url:String){
        Log.i(TAG, "Got a URL : $url")

        requestMap[target] = url

        //새로운 메세지를 백그라운드 스레드의 메세지 큐로 넣음
        //메세지 what: MESSAGE_DOWNLOAD, obj: queueThumbnail(...)에 전달되는 T타입 객체
        //새로운 메세지는 지정된 T타입 객체의 내려받기 요청을 나타냄
        //requestHanlder로부터 메세지를 직업 얻음 & 새로운 Message 객체의 target 속성 requestHandler로 자동설정
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)
            .sendToTarget()
    }

    private fun handleRequest(target: T){
        //내려받기를 수행하는 함수
        //URL이 있는지 확인하고, 해당 URL을 flickrFetchr.fetchPhoto(...)의 인자로 전달
        val url = requestMap[target]?:return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return
    }
}