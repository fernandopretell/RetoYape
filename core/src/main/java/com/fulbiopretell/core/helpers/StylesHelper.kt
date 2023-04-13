package com.fulbiopretell.retoyape.core.helpers

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.fulbiopretell.retoyape.core.R

class StylesHelper(private val context: Context) {

    fun updateCardViewStyle(cardView: CardView, cardColor: Int, cardRadius: Float, cardElevation: Float? = null) {
        cardView.radius = cardRadius
        cardView.setCardBackgroundColor(cardColor)
        cardElevation?.let { cardView.cardElevation = it }
    }

    fun updateTextViewStyle(textView: TextView, typeFace: Typeface?, textColor: Int?, textSize: Float) {
        textView.typeface = typeFace
        textView.textSize = pixelsToSp(textSize)
        textColor?.let { textView.setTextColor(textColor) }
    }

    fun pixelsToSp(px: Float) = px / context.resources.displayMetrics.scaledDensity

    fun dpToPx(dps: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, context.resources.displayMetrics).toInt()
    }

    fun getFont(fontId: Int): Typeface? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.resources.getFont(fontId)
        } else {
            ResourcesCompat.getFont(context, fontId)
        }
    }

}
