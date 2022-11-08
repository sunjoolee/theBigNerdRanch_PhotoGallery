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
        return Result.success()
    }
}