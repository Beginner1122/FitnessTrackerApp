package com.example.fitnesstrackerapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity(),SensorEventListener {

  private lateinit var sensorManager: SensorManager
    var countDownTimer: CountDownTimer? = null


    private var totalSteps:Float = 0f
    private var prevTotalStep:Float=0f
    private var running = false
    private var totaldistance=0f
    private var totalCalorie=0f
    private var prevCalorie=0f
    private var min:Float=0f
    private var datavalues:ArrayList<Entry> = ArrayList<Entry>()
    var lineDataset=LineDataSet(datavalues,"linedataset")
    var dataset:ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
    lateinit var data : LineData


    lateinit var lineChart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        permission()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var delete:ImageButton=findViewById(R.id.delete)
        var steps:TextView=findViewById(R.id.steps)
        var distance:TextView=findViewById(R.id.distance)
        var calories:TextView=findViewById(R.id.calories)
        distance.text="%.2f m".format(totaldistance)
        calories.text="%.2f cal".format(totalCalorie)

        lineChart=findViewById(R.id.chart)


        lineDataset=LineDataSet(datavalues,"linedataset")

        dataset.add(lineDataset)
        data =LineData(dataset)
        lineChart.data=data
        lineChart.invalidate()

        delete.setOnClickListener {
            Toast.makeText(this, "Button works", Toast.LENGTH_SHORT).show()
            prevTotalStep = totalSteps
            steps.text=0.toString()
            distance.text="00 m"
            calories.text="00 cal"
            datavalues.clear()

            lineChart.invalidate()

        }
        startTimer()



            }

    fun startTimer() {
        var cal:Float=0f
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish(){

                cal=totalCalorie-prevCalorie
                datavalues.add(Entry(min,cal))
                prevCalorie=totalCalorie
                min=min.inc()
                 lineDataset=LineDataSet(datavalues,"linedataset")

                dataset.add(lineDataset)
                data =LineData(dataset)
                lineChart.data=data
                lineChart.invalidate()
                startTimer()
            }
        }.start() }


    fun permission(){
        Dexter.withContext(this).withPermission(android.Manifest.permission.ACTIVITY_RECOGNITION).withListener(
            object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(permission: com.karumi.dexter.listener.PermissionRequest, token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }

            }).check()

    }



    override fun onSensorChanged(event: SensorEvent?) {
        var steps:TextView=findViewById(R.id.steps)
        var distance:TextView=findViewById(R.id.distance)
        var calories:TextView=findViewById(R.id.calories)


        if (running) {
            totalSteps = event!!.values[0]

            val currentSteps = totalSteps.toInt() - prevTotalStep.toInt()

            steps.text=currentSteps.toString()
            totaldistance=(currentSteps.toFloat())/1.32f
            distance.text="%.2f m".format(totaldistance)
            totalCalorie=(currentSteps.toFloat())*0.04f
            calories.text="%.2f cal".format(totalCalorie)

        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        running=true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor==null){
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }
        else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }




}