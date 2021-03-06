package com.hustunique.coolface.bean

import cn.bmob.v3.BmobUser

/**
 * 用户
 */
data class User(var nickname: String, var avatar: String, var posts: List<Post>) : BmobUser()