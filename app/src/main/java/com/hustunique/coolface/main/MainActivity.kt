package com.hustunique.coolface.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    val CAMERA_CODE = 666
    private lateinit var mViewModel: MainViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as MainViewModel
        mViewModel.init()
    }

    override fun initView() {
        super.initView()
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        main_list.adapter = MainAdapter()
    }


    override fun initContact() {
        super.initContact()
        mViewModel.posts.observe(this, Observer {
            (main_list.adapter as MainAdapter).data = it
            (main_list.adapter as MainAdapter).notifyDataSetChanged()
        })
        mainactivity_fb.setOnClickListener {
            startCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

        }
    }

    private fun startCamera() {
        val file = mViewModel.getPictureFile(applicationContext)
        file?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                val imgUri = FileProvider.getUriForFile(this, FileUtil.FILE_PROVIDER_AUTHORITY, it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, CAMERA_CODE)
            }
        }
    }
}
