package com.androidx.helpdesk.burnOutChart

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityBurnOutChartScreenBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.json.JSONException
import org.json.JSONObject
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BurnOutChartScreen : AppCompatActivity() {

    private var binding: ActivityBurnOutChartScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var sprintId = 0

    private var taskDate: String? = null

    private var allottedHours: Double? = null

    private var actualHours: Double? = null

    private val dateList = mutableListOf<String>()

    private val actualWorkList = mutableListOf<Float>()

    private val actualEntries = ArrayList<Entry>()

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_burn_out_chart_screen)
        bundleData()
        getBurnOutChartData()
        initListener()

        binding!!.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    val date = formatter.format(Date(e.x.toLong()))
                    val value = e.y.toInt()
                    showAlert(date, value)
                }
            }

            override fun onNothingSelected() {
            }
        })
    }

    private fun bundleData()
    {
        val intent = intent
        sprintId = intent.getIntExtra("sprintId", 0)


    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
        }
    }

    private fun getBurnOutChartData()
    {
        binding!!.cardView.visibility = View.VISIBLE
        dateList.clear()
        actualWorkList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.burnOutChart + sprintId,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    Log.d("sprint",sprintId.toString())

                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                taskDate = loginObject.getString("TaskDate")
                                allottedHours = loginObject.getDouble("AllotedHours")
                                actualHours = loginObject.getDouble("ActualWorkedHours")



                                    val separatedDate = taskDate!!.split('T')[0]
                                    dateList.add(separatedDate)
                                    actualWorkList.add(actualHours!!.toFloat())
                                    populateChart(dateList, actualWorkList)
                            }
                        }
                    }else{
                        binding!!.chart.clear()
                        binding!!.chart.setNoDataText("No Chart Data Available")
                        binding!!.chart.setNoDataTextColor(Color.WHITE)
                        binding!!.chart.setNoDataTextTypeface(ResourcesCompat.getFont(this, R.font.just_sans_semibold))
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun showAlert(date: String, value: Int) {
        AlertDialog.Builder(this)
            .setTitle("Worked Hours..!")
            .setMessage("Date: $date\nValue: $value")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun populateChart(dates: List<String>, actualWork: List<Float>) {
        actualEntries.clear()

        // Return if the first value is 0


        for (i in actualWork.indices) {
            val date = formatter.parse(dates[i])
            if (date != null) {
                val xValue = date.time.toFloat()
                actualEntries.add(Entry(xValue, actualWork[i]))
            }
        }




        val actualDataSet = LineDataSet(actualEntries, "Actual Work")
        actualDataSet.color = Color.WHITE
        actualDataSet.lineWidth = 2f
        actualDataSet.setDrawValues(true)
        actualDataSet.valueTextSize = 10f
        actualDataSet.valueTextColor = Color.BLACK

        val data = LineData(actualDataSet)
        binding!!.chart.data = data

        val xAxis: XAxis = binding!!.chart.xAxis
        xAxis.valueFormatter = DateAxisValueFormatter(data) // Pass the dateMap
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // Ensure only actual dates are displayed
        xAxis.setDrawGridLines(false) // Disable vertical grid lines
        xAxis.axisLineColor = Color.BLACK
        xAxis.axisLineWidth = 1.6f
        xAxis.labelCount = 2 // Ensure only 2 labels are visible
        val yAxis: YAxis = binding!!.chart.axisLeft
        yAxis.granularity = 5f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 10f
        yAxis.axisLineColor = Color.BLACK
        yAxis.axisLineWidth = 1.6f
        yAxis.setDrawGridLines(false) // Disable horizontal grid lines

        binding!!.chart.axisRight.isEnabled = false



        // Add LimitLines at the desired date points
        for (i in actualWork.indices) {
            val date = formatter.parse(dates[i])

            if (i == actualWork.size - 1 ){
                val limitLine = LimitLine(date.time.toFloat(), dates[i])
                limitLine.lineColor = Color.GRAY
                limitLine.lineWidth = 1f
                limitLine.textColor = Color.BLACK
                limitLine.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
                limitLine.textSize = 10f
                xAxis.addLimitLine(limitLine)
            }
            if (date != null) {
                val limitLine = LimitLine(date.time.toFloat(), dates[i])
                limitLine.lineColor = Color.GRAY
                limitLine.lineWidth = 1f
                limitLine.textColor = Color.BLACK
                limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                limitLine.textSize = 10f
                xAxis.addLimitLine(limitLine)
            }
        }
        binding!!.chart.invalidate() // Refresh the chart
    }


    class DateAxisValueFormatter(private val data: LineData) : ValueFormatter() {
        private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val position = value.toInt()
            if (position >= 0 && position < data.dataSetCount) {
                val entry = data.getDataSetByIndex(0).getEntryForIndex(position)
                val dateInMillis = entry.x.toLong()
                return sdf.format(Date(dateInMillis))
            }
            return ""
        }
    }
}