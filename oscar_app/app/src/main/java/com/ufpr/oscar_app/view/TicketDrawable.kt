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
 * semicirculares vazados nas laterais, deixando o fundo da tela aparecer através deles.
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

    /**
     * Reconstrói o contorno do ticket quando o tamanho muda.
     */
    override fun onBoundsChange(bounds: Rect) {
        construirPath(bounds)
    }

    /**
     * Monta o path do ticket subtraindo os dois círculos laterais do retângulo.
     */
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

        path.op(entalheEsquerdo, Path.Op.DIFFERENCE)
        path.op(entalheDireito, Path.Op.DIFFERENCE)
    }

    /**
     * Desenha o preenchimento e a borda do ticket.
     */
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
