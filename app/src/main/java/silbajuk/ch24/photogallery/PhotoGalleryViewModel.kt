package silbajuk.ch24.photogallery

import android.app.Application
import androidx.lifecycle.*
import retrofit2.http.Query

class PhotoGalleryViewModel(private val app : Application) : AndroidViewModel(app) {

    val galleryItemLiveData : LiveData<List<GalleryItem>>

    private val fLickrFetchr = FLickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init{
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        galleryItemLiveData =
            Transformations.switchMap(mutableSearchTerm){searchTerm ->
                if(searchTerm.isBlank()){
                    fLickrFetchr.fetchPhotos()
                } else{
                    fLickrFetchr.searchPhotos(searchTerm)
                }
            }
    }

    fun fetchPhotos(query: String = ""){
        QueryPreferences.setStoredQuery(app,query)
        mutableSearchTerm.value = query
    }

}