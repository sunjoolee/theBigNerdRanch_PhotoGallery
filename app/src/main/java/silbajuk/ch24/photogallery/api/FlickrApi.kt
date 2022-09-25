package silbajuk.ch24.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi {
    @GET(
        "/services/rest/?method=flickr.interestingness.getList"
                +"&api_key=d777e755025bdcbc63e813cba066c618"
                +"&extras=url_s"
                +"&format=json"
                +"&nojsoncallback=1")
    fun fetchPhotos(): Call<FlickrResponse>
}