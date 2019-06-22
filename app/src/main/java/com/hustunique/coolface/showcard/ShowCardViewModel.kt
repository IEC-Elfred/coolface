package com.hustunique.coolface.showcard

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.ui.widget.DanmakuView

class ShowCardViewModel : ViewModel() {
    private var post: Post? = null
    val comments = MutableLiveData<List<String>>()

    fun init(post: Post?) {
        this.post = post
        // TODO: 根据post查询到其对应的评论
        comments.postValue(
            listOf(
                "可以啊",
                "郭老师牛逼",
                "小岳岳我爱你",
                "我家胡歌最帅了"
            )
        )
    }

    fun showDanmu(danmaku: BaseDanmaku?, dmView: DanmakuView) {
        dmView.addDanmaku(danmaku)
    }


    fun addDanmu(content: String, context: Context, dmContext: DanmakuContext, dmView: DanmakuView) {
        // TODO: 将弹幕的内容添加到数据结构中

        if (dmView.isPaused)
            dmView.resume()
        showDanmu(createDanmu(dmContext, dmView.currentTime, content, Color.RED, Color.GREEN), dmView)
    }

    fun getDmContext(): DanmakuContext {
        val maxLines = HashMap<Int, Int>()
        maxLines[BaseDanmaku.TYPE_SCROLL_RL] = 4

        val overMap = HashMap<Int, Boolean>()
        overMap[BaseDanmaku.TYPE_SCROLL_RL] = true
        overMap[BaseDanmaku.TYPE_FIX_TOP] = true

        val context = DanmakuContext.create()
        context.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f).setDuplicateMergingEnabled(true)
            .setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f).setMaximumLines(maxLines).preventOverlapping(overMap)
            .setDanmakuMargin(50)
        context.isDuplicateMergingEnabled = true
        return context
    }

    fun getParser() = object : BaseDanmakuParser() {
        override fun parse(): IDanmakus = Danmakus()
    }

    /**
     * 默认的创建弹幕
     */
    fun createDanmu(context: Context, dmContext: DanmakuContext, time: Long, content: String): BaseDanmaku? =
        createDanmu(
            dmContext,
            time,
            content,
            context.getColor(R.color.colorPrimaryDark),
            context.getColor(R.color.colorAccent)
        )

    /**
     * 可配置颜色的创建弹幕
     */
    fun createDanmu(
        dmContext: DanmakuContext,
        time: Long,
        content: String,
        textColor: Int,
        borderColor: Int
    ): BaseDanmaku? {
        val danmaku = dmContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL) ?: return null
        danmaku.text = content
        danmaku.padding = 5
        danmaku.priority = 1
        danmaku.isLive = true
        danmaku.time = time + 2000
        danmaku.textSize = 60f
        danmaku.textColor = textColor
        danmaku.borderColor = borderColor

        return danmaku
    }

}