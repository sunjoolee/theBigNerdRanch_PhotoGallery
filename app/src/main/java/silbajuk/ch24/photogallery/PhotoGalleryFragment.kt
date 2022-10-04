package silbajuk.ch24.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.CollapsibleActionView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import silbajuk.ch24.photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //프래스먼트 유보(retain)
        //사용자가 장치를 회전하여 구성 변경이 생길 때도 그 시점의 프래그먼트 인스턴스가 갖고 있던 상태 데이터를 계속 보존
        retainInstance = true

       photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

        //응답 Handler 연결하기
        //Handler는 현재 스레드의 Looper에 자신을 연결 -> main 스레드와 연결된 Handler 생성됨
        val responseHandler = Handler()
        thumbnailDownloader =
            ThumbnailDownloader(responseHandler){photoHolder, bitmap->
                val drawable = BitmapDrawable(resources,bitmap)
                photoHolder.bindDrawable(drawable)
            }

        //프래그먼트 LifecycleObserver 등록 코드 변경
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //프래그먼트 뷰 LifecycleObserver 등록 코드 추가
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )

        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context,3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                Log.d(TAG, "Have gallery items from ViewModel $galleryItems")
                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //프래그먼트 뷰 LifecycleObserver 등록 해제 추가
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        //프래그먼트 LifecycleObserver 등록 해제 변경
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    companion object{
        //PhotoGalleryActivity 에서 새 프래그먼트 인스턴스 생성할 때 호출
        fun newInstance() = PhotoGalleryFragment()
    }

    private class PhotoHolder(private val itemImageView: ImageView)
        : RecyclerView.ViewHolder(itemImageView){
        val bindDrawable : (Drawable) -> Unit = itemImageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val galleryItems : List<GalleryItem>)
        : RecyclerView.Adapter<PhotoHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
           val galleryItem = galleryItems[position]

            //이미지를 내려받아 교체할 때까지 각 ImageView에 임시로 보여줄 이미지
            val placeholder:Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            )?: ColorDrawable()

            holder.bindDrawable(placeholder)

            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }

        override fun getItemCount(): Int = galleryItems.size
        }
}