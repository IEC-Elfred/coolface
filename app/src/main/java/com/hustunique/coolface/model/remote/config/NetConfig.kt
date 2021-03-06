package com.hustunique.coolface.model.remote.config

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
object NetConfig {
    const val FACEPP_BASE_URL = "https://api-cn.faceplusplus.com/"
    const val FACEPP_METHOD_DETECT = "facepp/v3/detect"
    const val FACEPP_METHOD_ADDTO_FACESET = "facepp/v3/faceset/addface"
    const val FACEPP_METHOD_REMOVEFROM_FACESET = "facepp/v3/faceset/removeface"
    const val FACEPP_METHOD_BEAUTIFY = "facepp/v1/beautify"
    const val FACEPP_METHOD_SEARCH = "facepp/v3/search"
    const val FACEPP_MEtHOD_MERGE = "imagepp/v1/mergeface"

    const val SMMS_BASE_URL = "https://sm.ms/api/"
    const val SMMS_METHOD_UPLOAD = "upload"
    const val SMMS_METHOD_DELETE = "delete/"

    const val BMOB_BASE_URL = "https://api2.bmob.cn/1/"
    const val BMOB_METHOD_CLASS = "classes/"

}