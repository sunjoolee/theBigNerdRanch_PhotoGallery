package silbajuk.ch24.photogallery

import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.*

private const val TAG = "ThumbnailDownloader"

class ThumbnailDownloader<in T> : HandlerThread(TAG), DefaultLifecycleObserver {
    private var hasQuit = false

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
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.i(TAG, "Destroying background Thread")
    }

    //이 함수는 PhotoAdapter의 onBindViewHolder(...)에서 호출됨
    //target: 내려받기의 식별자로 사용하기 위한 T 타입 객체
    fun queueThumbnail(target : T, url:String){
        Log.i(TAG, "Got a URL : $url")
    }
}