package silbajuk.ch24.photogallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        val isFragmentContainerEmpty = (savedInstanceState == null)
        if(isFragmentContainerEmpty){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }

    companion object{
        fun newIntent(context: Context): Intent {
            //PhotoGalleryActivity를 시작시키는 Intent인스턴스 반환
            return Intent(context, PhotoGalleryActivity::class.java)
            //PollWorker가 PhotoGalleryActivity.Intent(...)를 호출하고
            //결과로 반환된 인텐트를 Pending Intent에 포함한 후 알람 설정
        }
    }
}