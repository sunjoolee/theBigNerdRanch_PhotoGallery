package silbajuk.ch24.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import silbajuk.ch24.photogallery.api.FlickrApi

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrofit 인스턴스를 사용해서 FlickrApi 인스턴스 생성하기
        val retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .build()

        val flickrApi : FlickrApi = retrofit.create(FlickrApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context,3)

        return view
    }

    companion object{
        //PhotoGalleryActivity에서 새 프래그먼트 인스턴스 생성할 때
        fun newInstance() = PhotoGalleryFragment()
    }
}