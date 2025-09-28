package com.esc.begu.mediapipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.esc.begu.R
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class PoseOverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->

            // Calculate scaled image dimensions
            val scaledImageWidth = imageWidth * scaleFactor
            val scaledImageHeight = imageHeight * scaleFactor

            // Calculate offsets to center the image on the canvas
            val offsetX = (width - scaledImageWidth) / 2f
            val offsetY = (height - scaledImageHeight) / 2f

            val index = listOf(11, 13, 15, 12, 14, 16)                      // For filtered points

            for (landmark in poseLandmarkerResult.landmarks()) {
//                for (normalizedLandmark in landmark) {                      // For all points
                for ((i, normalizedLandmark) in landmark.withIndex()) {     // for filtered points
                    if (i in index) {                                       // for filtered points
                        canvas.drawPoint(
                            normalizedLandmark.x() * scaledImageWidth + offsetX,
                            normalizedLandmark.y() * scaledImageHeight + offsetY,
                            pointPaint
                        )
                    }
                }

                PoseLandmarker.POSE_LANDMARKS.forEach {
                    if ( (it.start() in index) && (it.end() in index) ) {   // for filtered points
                        canvas.drawLine(
                            poseLandmarkerResult.landmarks()[0][it.start()].x() * scaledImageWidth + offsetX,
                            poseLandmarkerResult.landmarks()[0][it.start()].y() * scaledImageHeight + offsetY,
                            poseLandmarkerResult.landmarks()[0][it.end()].x() * scaledImageWidth + offsetX,
                            poseLandmarkerResult.landmarks()[0][it.end()].y() * scaledImageHeight + offsetY,
                            linePaint
                        )
                    }
                }
            }
        }
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }

}