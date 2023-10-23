package com.example.inclinometer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(MyView(this))

    }

    inner class MyView(context: Context): View(context), SensorEventListener2 {

        val mRotMatrix = FloatArray(9)
        val mRotVector = FloatArray(3)
        val mOrientation = FloatArray(3)

        var canvasHeigth = 0
        var canvasWidth = 0

        val ctrScreenRoll=FloatArray(2)
        val ctrScreenPitch=FloatArray(2)
        val ctrImgRoll = FloatArray(2)
        val ctrImgPitch = FloatArray(2)


        val mRollMatrix = Matrix()
        val mPitchMatrix = Matrix()

        val mTRollMatrix = Matrix()
        val mTPitchMatrix = Matrix()

        val textPainter= Paint().also{
            it.textSize=50f
            it.color= Color.BLACK
            it.strokeWidth=5f
        }


         var jeep : Bitmap
         var jeepSideView : Bitmap

        init {
            (context.getSystemService(
                Context.SENSOR_SERVICE
            ) as SensorManager).also {
                it.registerListener(this,
                    it.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                    SensorManager.SENSOR_DELAY_NORMAL)
            }
            jeep = BitmapFactory.
            decodeStream(context.assets.open("jeepBackView.png"))
            ctrImgRoll[0] = jeep.width/2f
            ctrImgRoll[1] = jeep.height/2f
            jeepSideView = BitmapFactory.
            decodeStream(context.assets.open("jeepSideView.png"))
            ctrImgPitch[0] = jeepSideView.width/2f
            ctrImgPitch[1] = jeepSideView.height/2f
        }

        override fun onSensorChanged(p0: SensorEvent?) {
            mRotVector[0] = p0?.values?.get(0)?:0f
            mRotVector[1] = p0?.values?.get(1)?:0f
            mRotVector[2] = p0?.values?.get(2)?:0f
            SensorManager.getRotationMatrixFromVector(mRotMatrix,mRotVector)
            SensorManager.getOrientation(mRotMatrix, mOrientation)
            invalidate()
            // TODO("Not yet implemented")
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
          //  TODO("Not yet implemented")
        }

        override fun onFlushCompleted(p0: Sensor?) {
          //  TODO("Not yet implemented")
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.apply {
                drawRGB(255, 255, 255)
                drawText("Yaw: "+ mOrientation[0], 10f, 100f, textPainter)
                drawText("Pitch: "+ mOrientation[1], 10f, 150f, textPainter)
                drawText("Roll: "+ mOrientation[2], 10f, 200f, textPainter)
            }

            mRollMatrix.apply {
                reset()
                postConcat(mTRollMatrix)
                postRotate(((-180/Math.PI)*mOrientation[2]).toFloat(),
                    ctrScreenRoll[0], ctrScreenRoll[1])
            }

            mPitchMatrix.apply {
                reset()
                postConcat(mTPitchMatrix)
                postRotate(((-180/Math.PI)*mOrientation[1]).toFloat(),
                    ctrScreenPitch[0], ctrScreenPitch[1])
            }

            canvas.apply {
                drawBitmap(jeep,mRollMatrix,null)
                drawBitmap(jeepSideView,mPitchMatrix,null)
            }

        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            canvasWidth=w
            canvasHeigth=h
            var imgCenter = FloatArray(2)
            imgCenter[0]=jeep.width/2f
            imgCenter[1]=jeep.height/2f
            ctrScreenRoll[0]=w/2f
            ctrScreenRoll[1]=h/2f
            mTRollMatrix.setPolyToPoly(
                ctrImgRoll,0,
                ctrScreenRoll,0,
                1)

            ctrScreenPitch[0]=w/2f
            ctrScreenPitch[1]=h/6f
            mTPitchMatrix.
            setPolyToPoly(ctrImgPitch,0,ctrScreenPitch,0,1)
        }
    }
}