package silbajuk.ch24.photogallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

private const val ARG_URI = "photo_page_url"

class PhotoPageFragment : VisibleFragment(){
    private lateinit var uri : Uri
    private lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_page, container, false)
        webView = view.findViewById(R.id.web_view)

        return view
    }

    companion object{
        fun newInstance(uri: Uri) : PhotoPageFragment{
            return PhotoPageFragment().apply{
                arguments = Bundle().apply{
                    putParcelable(ARG_URI, uri)
                }
            }
        }
    }
}