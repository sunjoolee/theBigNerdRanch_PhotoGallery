package silbajuk.ch24.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse>

    //URL 문자열을 인자로 받아 실행 가능한 Call 객체를 반환하는 함수
    @GET
    fun fetchUrlBytes(@Url url:String):Call<ResponseBody>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query :String) : Call<FlickrResponse>
}