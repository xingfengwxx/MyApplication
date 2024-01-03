package com.example.myapplication

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatTextView


/**
 * author : 王星星
 * date : 2023/7/19 10:22
 * email : 1099420259@qq.com
 * description :
 */


class DownloadProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    interface OnDownLoadClickListener {
        fun clickDownload()
        fun clickPause()
        fun clickResume()
        fun clickFinish()
    }

    class SimpleOnDownLoadClickListener : OnDownLoadClickListener {
        override fun clickDownload() {}
        override fun clickPause() {}
        override fun clickResume() {}
        override fun clickFinish() {}
    }

    //背景画笔
    private var mBackgroundPaint: Paint? = null

    //背景边框画笔
    private var mBackgroundBorderPaint: Paint? = null

    //按钮文字画笔
    @Volatile
    private var mTextPaint: Paint? = null

    //背景颜色
    private var mBackgroundColor = 0

    //下载中后半部分后面背景颜色
    private var mBackgroundSecondColor = 0

    //文字颜色
    private var mTextColor = 0

    //覆盖后颜色
    private var mTextCoverColor = 0
    private var mProgress = -1f
    private var mToProgress = 0f
    private var mMaxProgress = 0
    private var mMinProgress = 0
    private var mProgressPercent = 0f
    private var mButtonRadius = 0f
    private var mBackgroundBounds: RectF? = null
    private var mProgressBgGradient: LinearGradient? = null
    private var mProgressTextGradient: LinearGradient? = null
    private var mProgressAnimation: ValueAnimator? = null
    private var mCurrentText: CharSequence? = null
    private var mState = -1
    private var backgroud_strokeWidth //边框宽度
            = 0f
    private var mNormalText: String? = null
    private var mDowningText: String? = null
    private var mFinishText: String? = null
    private var mPauseText: String? = null
    private var mAnimationDuration: Long = 0
    private var mOnDownLoadClickListener: OnDownLoadClickListener? = null
    private var mEnablePause = false

    init {
        if (!isInEditMode) {
            initAttrs(context, attrs)
            init()
            setupAnimations()
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton)
        mBackgroundColor = a.getColor(
            R.styleable.DownloadProgressButton_background_color,
            Color.parseColor("#6699ff")
        )
        mBackgroundSecondColor =
            a.getColor(R.styleable.DownloadProgressButton_background_second_color, Color.LTGRAY)
        mButtonRadius =
            a.getFloat(R.styleable.DownloadProgressButton_radius, (measuredHeight / 2).toFloat())
        mTextColor = a.getColor(R.styleable.DownloadProgressButton_text_color, mBackgroundColor)
        mTextCoverColor =
            a.getColor(R.styleable.DownloadProgressButton_text_cover_color, Color.WHITE)
        backgroud_strokeWidth =
            a.getDimension(R.styleable.DownloadProgressButton_background_strokeWidth, 3f)
        mNormalText = a.getString(R.styleable.DownloadProgressButton_text_normal)
        mDowningText = a.getString(R.styleable.DownloadProgressButton_text_downing)
        mFinishText = a.getString(R.styleable.DownloadProgressButton_text_finish)
        mPauseText = a.getString(R.styleable.DownloadProgressButton_text_pause)
        mAnimationDuration =
            a.getInt(R.styleable.DownloadProgressButton_animation_duration, 500).toLong()
        a.recycle()
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        mTextPaint!!.textSize = textSize
        invalidate()
    }

    private fun init() {
        mMaxProgress = 100
        mMinProgress = 0
        mProgress = 0f
        if (mNormalText == null) {
            mNormalText = "下载"
        }
        if (mDowningText == null) {
            mDowningText = "下载中"
        }
        if (mFinishText == null) {
            mFinishText = "打开"
        }
        if (mPauseText == null) {
            mPauseText = "继续"
        }
        //设置背景画笔
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.isAntiAlias = true
        mBackgroundPaint!!.style = Paint.Style.FILL
        mBackgroundBorderPaint = Paint()
        mBackgroundBorderPaint!!.isAntiAlias = true
        mBackgroundBorderPaint!!.style = Paint.Style.STROKE
        mBackgroundBorderPaint!!.strokeWidth = backgroud_strokeWidth
        mBackgroundBorderPaint!!.color = mBackgroundColor
        //设置文字画笔
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //解决文字有时候画不出问题
            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint)
        }

        //初始化状态设为NORMAL
        setState(NORMAL)
        setOnClickListener(OnClickListener {
            if (mOnDownLoadClickListener == null) {
                return@OnClickListener
            }
            if (getState() == NORMAL) {
                mOnDownLoadClickListener!!.clickDownload()
                setState(DOWNLOADING)
                setProgressText(0)
            } else if (getState() == DOWNLOADING) {
                if (mEnablePause) {
                    mOnDownLoadClickListener!!.clickPause()
                    setState(PAUSE)
                }
            } else if (getState() == PAUSE) {
                mOnDownLoadClickListener!!.clickResume()
                setState(DOWNLOADING)
                setProgressText(mProgress.toInt())
            } else if (getState() == FINISH) {
                mOnDownLoadClickListener!!.clickFinish()
            }
        })
    }

    private fun setupAnimations() {
        mProgressAnimation = ValueAnimator.ofFloat(0f, 1f).setDuration(mAnimationDuration)
        mProgressAnimation?.addUpdateListener(AnimatorUpdateListener { animation ->
            val timePercent = animation.animatedValue as Float
            mProgress = (mToProgress - mProgress) * timePercent + mProgress
            setProgressText(mProgress.toInt())
        })
        mProgressAnimation?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (mToProgress < mProgress) {
                    mProgress = mToProgress
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (mProgress == mMaxProgress.toFloat()) {
                    setState(FINISH)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInEditMode) {
            drawing(canvas)
        }
    }

    private fun drawing(canvas: Canvas) {
        drawBackground(canvas)
        drawTextAbove(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        if (mBackgroundBounds == null) {
            mBackgroundBounds = RectF()
            if (mButtonRadius == 0f) {
                mButtonRadius = (measuredHeight / 2).toFloat()
            }
            mBackgroundBounds!!.left = backgroud_strokeWidth
            mBackgroundBounds!!.top = backgroud_strokeWidth
            mBackgroundBounds!!.right = measuredWidth - backgroud_strokeWidth
            mBackgroundBounds!!.bottom = measuredHeight - backgroud_strokeWidth
        }
        when (mState) {
            NORMAL -> {}
            DOWNLOADING, PAUSE -> {
                mProgressPercent = mProgress / (mMaxProgress + 0f)
                mProgressBgGradient = LinearGradient(
                    backgroud_strokeWidth,
                    0f,
                    measuredWidth - backgroud_strokeWidth,
                    0f,
                    intArrayOf(mBackgroundColor, mBackgroundSecondColor),
                    floatArrayOf(mProgressPercent, mProgressPercent + 0.001f),
                    Shader.TileMode.CLAMP
                )
                mBackgroundPaint!!.color = mBackgroundColor
                mBackgroundPaint!!.shader = mProgressBgGradient
                canvas.drawRoundRect(
                    mBackgroundBounds!!, mButtonRadius, mButtonRadius,
                    mBackgroundPaint!!
                )
            }
            FINISH -> {
                mBackgroundPaint!!.shader = null
                mBackgroundPaint!!.color = mBackgroundColor
                canvas.drawRoundRect(
                    mBackgroundBounds!!, mButtonRadius, mButtonRadius,
                    mBackgroundPaint!!
                )
            }
        }
        canvas.drawRoundRect(
            mBackgroundBounds!!, mButtonRadius, mButtonRadius,
            mBackgroundBorderPaint!!
        ) //绘制边框
    }

    private fun drawTextAbove(canvas: Canvas) {
        mTextPaint!!.textSize = textSize
        val y = canvas.height / 2 - (mTextPaint!!.descent() / 2 + mTextPaint!!.ascent() / 2)
        if (mCurrentText == null) {
            mCurrentText = ""
        }
        val textWidth = mTextPaint!!.measureText(mCurrentText.toString())
        when (mState) {
            NORMAL -> {
                mTextPaint!!.shader = null
                mTextPaint!!.color = mTextColor
                canvas.drawText(
                    mCurrentText.toString(), (measuredWidth - textWidth) / 2, y,
                    mTextPaint!!
                )
            }
            DOWNLOADING, PAUSE -> {
                val w = measuredWidth - 2 * backgroud_strokeWidth
                //进度条压过距离
                val coverlength = w * mProgressPercent
                //开始渐变指示器
                val indicator1 = w / 2 - textWidth / 2
                //结束渐变指示器
                val indicator2 = w / 2 + textWidth / 2
                //文字变色部分的距离
                val coverTextLength = textWidth / 2 - w / 2 + coverlength
                val textProgress = coverTextLength / textWidth
                if (coverlength <= indicator1) {
                    mTextPaint!!.shader = null
                    mTextPaint!!.color = mTextColor
                } else if (indicator1 < coverlength && coverlength <= indicator2) {
                    mProgressTextGradient = LinearGradient(
                        (w - textWidth) / 2 + backgroud_strokeWidth,
                        0f,
                        (w + textWidth) / 2 + backgroud_strokeWidth,
                        0f,
                        intArrayOf(mTextCoverColor, mTextColor),
                        floatArrayOf(textProgress, textProgress + 0.001f),
                        Shader.TileMode.CLAMP
                    )
                    mTextPaint!!.color = mTextColor
                    mTextPaint!!.shader = mProgressTextGradient
                } else {
                    mTextPaint!!.shader = null
                    mTextPaint!!.color = mTextCoverColor
                }
                canvas.drawText(
                    mCurrentText.toString(), (w - textWidth) / 2 + backgroud_strokeWidth, y,
                    mTextPaint!!
                )
            }
            FINISH -> {
                mTextPaint!!.color = mTextCoverColor
                canvas.drawText(
                    mCurrentText.toString(), (measuredWidth - textWidth) / 2, y,
                    mTextPaint!!
                )
            }
        }
    }

    fun getState(): Int {
        return mState
    }

    fun reset() {
        setState(NORMAL)
    }

    fun finish() {
        setState(FINISH)
    }

    private fun setState(state: Int) {
        if (mState != state) { //状态确实有改变
            mState = state
            if (state == FINISH) {
                setCurrentText(mFinishText)
                mProgress = mMaxProgress.toFloat()
            } else if (state == NORMAL) {
                mToProgress = mMinProgress.toFloat()
                mProgress = mToProgress
                setCurrentText(mNormalText)
            } else if (state == PAUSE) {
                setCurrentText(mPauseText)
            }
            invalidate()
        }
    }

    fun setCurrentText(charSequence: CharSequence?) {
        mCurrentText = charSequence
        invalidate()
    }

    fun getCurrentText(): CharSequence? {
        return mCurrentText
    }

    fun getProgress(): Float {
        return mProgress
    }

    fun setProgress(progress: Float) {
        if (progress <= mMinProgress || progress <= mToProgress || getState() == FINISH) {
            return
        }
        mToProgress = Math.min(progress, mMaxProgress.toFloat())
        setState(DOWNLOADING)
        if (mProgressAnimation!!.isRunning) {
            mProgressAnimation!!.end()
            mProgressAnimation!!.start()
        } else {
            mProgressAnimation!!.start()
        }
    }

    private fun setProgressText(progress: Int) {
        if (getState() == DOWNLOADING) {
            setCurrentText("$mDowningText$progress%")
        }
    }

    fun pause() {
        setState(PAUSE)
    }

    fun getButtonRadius(): Float {
        return mButtonRadius
    }

    fun setButtonRadius(buttonRadius: Float) {
        mButtonRadius = buttonRadius
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    override fun setTextColor(textColor: Int) {
        mTextColor = textColor
    }

    fun getTextCoverColor(): Int {
        return mTextCoverColor
    }

    fun setTextCoverColor(textCoverColor: Int) {
        mTextCoverColor = textCoverColor
    }

    fun getMinProgress(): Int {
        return mMinProgress
    }

    fun setMinProgress(minProgress: Int) {
        mMinProgress = minProgress
    }

    fun getMaxProgress(): Int {
        return mMaxProgress
    }

    fun setMaxProgress(maxProgress: Int) {
        mMaxProgress = maxProgress
    }

    fun getAnimationDuration(): Long {
        return mAnimationDuration
    }

    fun setAnimationDuration(animationDuration: Long) {
        mAnimationDuration = animationDuration
        mProgressAnimation!!.duration = animationDuration
    }

    fun getOnDownLoadClickListener(): OnDownLoadClickListener? {
        return mOnDownLoadClickListener
    }

    fun setOnDownLoadClickListener(onDownLoadClickListener: OnDownLoadClickListener?) {
        mOnDownLoadClickListener = onDownLoadClickListener
    }

    fun isEnablePause(): Boolean {
        return mEnablePause
    }

    fun setEnablePause(enablePause: Boolean) {
        mEnablePause = enablePause
    }

//    override fun onRestoreInstanceState(state: Parcelable) {
//        val ss = state as SavedState
//        super.onRestoreInstanceState(ss.superState)
//        mState = ss.state
//        mProgress = ss.progress.toFloat()
//        mCurrentText = ss.currentText
//    }
//
//    override fun onSaveInstanceState(): Parcelable {
//        val superState = super.onSaveInstanceState()
//        return SavedState(superState, mProgress.toInt(), mState, mCurrentText.toString())
//    }


    companion object {
        const val NORMAL = 1
        const val DOWNLOADING = 2
        const val PAUSE = 3
        const val FINISH = 4
    }
}