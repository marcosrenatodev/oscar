package com.ufpr.oscar_app.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable

/**
 * Fundo em forma de ticket de cinema: retângulo arredondado com dois entalhes
 * semicirculares recortados nas laterais (esquerda e direita, no centro vertical).
 *
 * Os entalhes são realmente vazados (transparentes), então o gradiente da tela
 * aparece através deles — diferente de um `<shape>`, que não consegue recortar.
 */
class TicketDrawable(
    private val fillColor: Int,
    private val strokeColor: Int,
    private val cornerRadius: Float,
    private val notchRadius: Float,
    private val strokeWidthPx: Float
) : Drawable() {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = fillColor
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = strokeColor
        strokeWidth = strokeWidthPx
    }

    private val path = Path()

    override fun onBoundsChange(bounds: Rect) {
        construirPath(bounds)
    }

    private fun construirPath(b: Rect) {
        val inset = strokeWidthPx / 2f
        val rect = RectF(
            b.left + inset,
            b.top + inset,
            b.right - inset,
            b.bottom - inset
        )

        path.reset()
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)

        val cy = rect.centerY()
        val entalheEsquerdo = Path().apply { addCircle(rect.left, cy, notchRadius, Path.Direction.CW) }
        val entalheDireito = Path().apply { addCircle(rect.right, cy, notchRadius, Path.Direction.CW) }

        // Subtrai os círculos do corpo do ticket, criando os recortes laterais.
        path.op(entalheEsquerdo, Path.Op.DIFFERENCE)
        path.op(entalheDireito, Path.Op.DIFFERENCE)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, strokePaint)
    }

    override fun setAlpha(alpha: Int) {
        fillPaint.alpha = alpha
        strokePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        fillPaint.colorFilter = colorFilter
        strokePaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
