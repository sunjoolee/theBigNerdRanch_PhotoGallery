package silbajuk.ch24.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import silbajuk.ch24.photogallery.api.FlickrApi
import silbajuk.ch24.photogallery.api.FlickrResponse

private const val TAG = "FlickrFetchr"

class FLickrFetchr{

    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos():LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch photos", t)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")

                //JSON 응답 데이터 파싱하여 GalleryItem 객체 리스트로 저장
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse:PhotoResponse? = flickrResponse?.photos
                var galleryItems : List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                //url_s 필드 값 없는 이미지 걸러냄
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }

                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    //인자로 전달된 URL로부터 데이터를 가져와서 Bitmap으로 변환
    @WorkerThread
    fun fetchPhoto(url:String): Bitmap?{
        val response : Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap = $bitmap from response $response")
        return bitmap
    }
}