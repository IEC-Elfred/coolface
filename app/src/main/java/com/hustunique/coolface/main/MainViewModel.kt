package com.hustunique.coolface.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo
import com.hustunique.coolface.util.LiveDataUtil

class MainViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    private lateinit var postRepo: PostRepo
    private lateinit var pictureRepo: PictureRepo

    /**
     * 表示现在在哪个状态下
     * 0：所有动态
     * 1：我的
     * 2：收藏
     * 3：排行榜
     */
    var status = 0

    private val posts: MutableList<Post> = ArrayList()

    /**
     * 整个列表更新
     */
    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    /**
     * 单个更新的Livedata
     */
    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()

    fun init(activity: MainActivity) {
        postRepo = PostRepo.getInstance()
        user.value = BmobUser.getCurrentUser(User::class.java)
        updatePosts(activity, 0)
        pictureRepo = PictureRepo.getInstance()
        pictureData.value = Resource.loading()
    }

    fun updatePosts(activity: MainActivity, status: Int = this.status) {
        when (status) {
            0 -> {
                updateAllPosts(activity) {
                    postsData.postValue(Resource.success(it))
                }
            }
            1 -> {
                updateMyPosts(activity)
            }
            2 -> {
                updateCollectPosts(activity)
            }
            3 -> {
                updateRankPosts(activity)
            }
        }
        this.status = status
    }

    /**
     * 显示排行榜
     */
    private fun updateRankPosts(activity: MainActivity) {
        updateAllPosts(activity) {
            postsData.postValue(Resource.success(it.sortedWith(Comparator { o1, o2 ->
                val o1Beauty = o1.face!!.attributes.beauty.let {
                    if (it.female_score > it.male_score)
                        it.female_score
                    else
                        it.male_score
                }
                val o2Beauty = o2.face!!.attributes.beauty.let {
                    if (it.female_score > it.male_score)
                        it.female_score
                    else
                        it.male_score
                }
                return@Comparator when {
                    o1Beauty < o2Beauty -> 1
                    o1Beauty == o2Beauty -> 0
                    else -> -1
                }
            })))
        }
    }

    private fun updateAllPosts(
        activity: MainActivity,
        onSuccess: (List<Post>) -> Unit = {}
    ) {
        activity.runOnUiThread {
            postRepo.getPosts(MutableLiveData<Resource<List<Post>>>().apply {
                observe(activity, Observer {
                    LiveDataUtil.useData(it, { postsList ->
                        posts.removeAll(posts)
                        posts.addAll(postsList!!)
                        onSuccess.invoke(posts)
                    })
                })
            })
        }
    }

    fun updatePostAt(position: Int) {
        if (position < postsData.value?.data?.size!!) {
            val targetPostId: String = postsData.value?.data?.get(position)?.objectId!!
            postRepo.getPost(targetPostId, postData)
        }
    }

    /**
     * 显示收藏的动态
     */
    private fun updateCollectPosts(activity: MainActivity) {
        updateAllPosts(activity) {
            postsData.postValue(Resource.success(
                it.filter {
                    it.favouriteUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false
                }
            ))
        }
    }

    fun deleteAt(
        activity: MainActivity,
        position: Int,
        onSuccess: () -> (Unit),
        onLoading: () -> (Unit),
        onError: (String) -> (Unit)
    ) {
        if (status == 1) {
            onLoading.invoke()
            postRepo.deletePost(postsData.value?.data?.get(position)?.objectId!!, {
                posts.remove(postsData.value?.data?.get(position)!!)
                updateMyPosts(activity)
                onSuccess.invoke()
            }, onError)
        }
    }

    /**
     * 显示我的动态
     */
    private fun updateMyPosts(activity: MainActivity) {
        updateAllPosts(activity) {
            postsData.postValue(Resource.success(it.filter {
                it.username == BmobUser.getCurrentUser(User::class.java).nickname
            }))
        }
    }

    fun like(positon: Int, onError: ((String) -> Unit)) {
        postsData.value?.data?.let {
            if (it[positon].likeUser == null) {
                it[positon].likeUser = ArrayList()
            }
            (it[positon].likeUser as MutableList).add(user.value?.username!!)
            postRepo.like(it[positon].objectId!!, null, onError)
        }
    }

    fun unLike(positon: Int, onError: ((String) -> Unit)) {
        postsData.value?.data?.let {
            (it[positon].likeUser as MutableList).apply {
                removeAt(indexOf(user.value?.username))
            }
            postRepo.unLike(it[positon].objectId!!, null, onError)
        }
    }

    fun upLoadAvatar(onError: (String) -> Unit) {
        pictureRepo.uploadUserAvatar({ url ->
            user.value?.let {
                it.avatar = url
                it.update(object : UpdateListener() {
                    override fun done(p0: BmobException?) {
                        if (p0 != null) {
                            onError(p0.errorCode.toString())
                        }
                    }
                })
                user.postValue(it)
            }
        }, onError)
    }

    fun getNewPictureFile() = PictureRepo.getInstance().getNewFile()

    fun getPictureFile() = pictureRepo.getFile()

    fun logOut() {
        BmobUser.logOut()
        user.postValue(null)
    }

}