package silbajuk.ch24.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import retrofit2.http.Query

class PhotoGalleryViewModel : ViewModel() {

    val galleryItemLiveData : LiveData<List<GalleryItem>>

    private val fLickrFetchr = FLickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    init{
        mutableSearchTerm.value = "sky"

        galleryItemLiveData =
            Transformations.switchMap(mutableSearchTerm){searchTerm ->
                fLickrFetchr.searchPhotos(searchTerm)
            }
    }

    fun fetchPhotos(query: String = ""){
        mutableSearchTerm.value = query
    }

}