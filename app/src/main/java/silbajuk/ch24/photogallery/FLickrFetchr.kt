package silbajuk.ch24.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import silbajuk.ch24.photogallery.api.FlickrApi
import silbajuk.ch24.photogallery.api.FlickrResponse
import silbajuk.ch24.photogallery.api.PhotoInterceptor

private const val TAG = "FlickrFetchr"

class FLickrFetchr{

    private val flickrApi: FlickrApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos():LiveData<List<GalleryItem>>{
        return fetchPhotoMetadata(flickrApi.fetchPhotos())
    }

    fun searchPhotos(query : String) : LiveData <List<GalleryItem>> {
        Log.d(TAG, "searchPhotos query: $query")
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }

    private fun fetchPhotoMetadata(flickrRequest : Call<FlickrResponse>)
        :LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch photos", t)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")

                //JSON 응답 데이터 파싱하여 GalleryItem 객체 리스트로 저장
                val flickrResponse: FlickrResponse? = response.body()
                Log.d(TAG, "flickrResponse: $flickrResponse")
                val photoResponse:PhotoResponse? = flickrResponse?.photos
                Log.d(TAG, "photoResponse: $photoResponse")
                var galleryItems : List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                Log.d(TAG, "galleryItems: $galleryItems")

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