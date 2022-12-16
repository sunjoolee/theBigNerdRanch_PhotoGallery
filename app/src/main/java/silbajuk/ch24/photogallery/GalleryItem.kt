package silbajuk.ch24.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName
import retrofit2.http.Url

data class GalleryItem(
    var title : String = "",
    var id : String = "",
    @SerializedName("url_s") var url : String = "",
    @SerializedName("owner") var owner : String = ""
){
    val photoPageUri: Uri
        get(){
            return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}