package silbajuk.ch24.photogallery

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker"

class PollWorker(val context: Context, workerParams: WorkerParameters)
    :Worker(context, workerParams){

    //doWork()함수는 백그라운드 스레드에서 호출됨
    override fun doWork(): Result {
        Log.i(TAG, "Work request triggered")

        val lastResultId = QueryPreferences.getLastResultId(context)
        val query = QueryPreferences.getStoredQuery(context)

        val items:List<GalleryItem> = if(query.isEmpty()){
            FLickrFetchr().fetchPhotosRequest()
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        }else{
            FLickrFetchr().searchPhotosRequest(query)
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        }?: emptyList()

        if(items.isEmpty()){
            return Result.success()
        }

        val resultId = items.first().id
        if(resultId == lastResultId){
            Log.i(TAG, "Got no new result. result id: $resultId")
        }else{
            Log.i(TAG, "Got a new result. result id: $resultId")
            QueryPreferences.setLastResultId(context, resultId)
        }

        return Result.success()
    }
}