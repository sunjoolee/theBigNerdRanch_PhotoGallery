package silbajuk.ch24.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
}