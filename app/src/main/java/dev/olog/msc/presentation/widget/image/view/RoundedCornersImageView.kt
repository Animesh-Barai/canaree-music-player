package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.utils.k.extension.dip

private const val DEFAULT_RADIUS = 5

open class RoundedCornersImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    private val radius : Int

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornersImageView)
        radius = a.getInt(R.styleable.RoundedCornersImageView_cornerRadius, DEFAULT_RADIUS)
        a.recycle()

        clipToOutline = true
        outlineProvider = RoundedOutlineProvider()

        if (!AppConstants.THEME.isFlat() && !AppConstants.THEME.isFullscreen() || !AppConstants.THEME.isBigImage()){
            val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner) as GradientDrawable
            drawable.cornerRadius = context.dip(radius).toFloat()
            background = drawable
        }
    }

}

private class RoundedOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val corner = view.context.dip(5).toFloat()
        outline.setRoundRect(0 , 0, view.width, view.height, corner)
    }
}