package com.androidx.helpdesk.adminCardView.weeklyAllotment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.weeklyAllotment.model.WeeklyTaskAllotmentModel
import java.text.SimpleDateFormat
import java.util.Locale

class WeeklyTaskAllotmentAdapter(private val context: Context?, private val weeklyTaskAllotmentModelList: List<WeeklyTaskAllotmentModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val emptyTaskAllotmentList = 1
    private val nonEmptyTaskAllotmentList = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            emptyTaskAllotmentList  -> {
                EmptyTaskDataHolder(layoutInflater.inflate(R.layout.weekly_task_allottment_empty_data, parent, false))
            }
            nonEmptyTaskAllotmentList ->  {
                NonEmptyTaskDataHolder(layoutInflater.inflate(R.layout.weekly_task_allottment_data, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return weeklyTaskAllotmentModelList.size
    }



    override fun getItemViewType(position: Int): Int
    {
        val weeklyTaskAllotmentModel: WeeklyTaskAllotmentModel = weeklyTaskAllotmentModelList[position]
        return if (weeklyTaskAllotmentModel.projectTaskId!!.equals(0) || weeklyTaskAllotmentModel.projectTaskId!! == 0)
        {
            emptyTaskAllotmentList
        } else {
            nonEmptyTaskAllotmentList
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentItem = weeklyTaskAllotmentModelList[position]
        val previousItem = if (position > 0) weeklyTaskAllotmentModelList[position - 1] else null

        when (holder.itemViewType)
        {
            emptyTaskAllotmentList ->
                (holder as EmptyTaskDataHolder).bind(
                    previousItem?.fullName ?: "",
                    previousItem?.allottedDate ?: "",
                    currentItem.allottedDate ?: "",
                    currentItem.fullName ?: "")
            nonEmptyTaskAllotmentList ->
                (holder as NonEmptyTaskDataHolder).bind(
                    previousItem?.fullName ?: "",previousItem?.allottedDate ?: "",
                    currentItem)
        }

    }

    private class EmptyTaskDataHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateLayout: ConstraintLayout
        var employeeLayout: ConstraintLayout
        var emptyDataLayout: CardView
        var dateTv: TextView
        var employeeNameTv: TextView

        init {
            dateLayout = itemView.findViewById(R.id.dateLayout)
            employeeLayout = itemView.findViewById(R.id.employeeLayout)
            emptyDataLayout = itemView.findViewById(R.id.emptyDataLayout)
            dateTv = itemView.findViewById(R.id.dateTv)
            employeeNameTv = itemView.findViewById(R.id.employeeNameTv)
        }

        @SuppressLint("SetTextI18n")
        fun bind(previousName: String,previousDate: String, date: String, empName: String)
        {
            dateLayout.visibility = View.GONE
            employeeLayout.visibility = View.GONE

            val showDateLayout = previousDate != date
            if (showDateLayout && date.isNotEmpty()) {
                dateLayout.visibility = View.VISIBLE
                val splitDateParts = emptyTaskDataFormatDateFormatDate(date).split(" ")
                dateTv.text = "${splitDateParts[0]}\n${splitDateParts[1]}"
            } else {
                dateLayout.visibility = View.GONE
            }


            val showEmployeeLayout = previousName != empName
            if (showEmployeeLayout && empName.isNotEmpty()) {
                employeeLayout.visibility = View.VISIBLE
                employeeNameTv.text = empName
            } else {
                employeeLayout.visibility = View.GONE
            }

            emptyDataLayout.visibility = View.VISIBLE
        }

        fun emptyTaskDataFormatDateFormatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""
            val outputFormat = SimpleDateFormat("d EEE", Locale.getDefault())
            return outputFormat.format(date)
        }
    }

    private class NonEmptyTaskDataHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateLayout: ConstraintLayout
        var employeeLayout: ConstraintLayout
        var nonEmptyDataLayout: CardView
        var projectName: TextView
        var moduleName: TextView
        var taskNameValue: TextView
        var allotedHourValue: TextView
        var taskStatusValue: TextView
        var timeSheetValue: TextView
        var workedHoursValue: TextView
        var dateTv: TextView
        var employeeNameTv: TextView

        init {
            dateLayout = itemView.findViewById(R.id.dateLayout)
            employeeLayout = itemView.findViewById(R.id.employeeLayout)
            nonEmptyDataLayout = itemView.findViewById(R.id.cardView)
            projectName = itemView.findViewById(R.id.projectName)
            moduleName = itemView.findViewById(R.id.moduleName)
            taskNameValue = itemView.findViewById(R.id.taskNameValue)
            allotedHourValue = itemView.findViewById(R.id.allotedHourValue)
            taskStatusValue = itemView.findViewById(R.id.taskStatusValue)
            timeSheetValue = itemView.findViewById(R.id.timeSheetValue)
            workedHoursValue = itemView.findViewById(R.id.workedHoursValue)
            dateTv = itemView.findViewById(R.id.dateTv)
            employeeNameTv = itemView.findViewById(R.id.employeeNameTv)
        }

        @SuppressLint("SetTextI18n")
        fun bind(previousName: String,previousDate: String, weeklyTaskAllotmentModel: WeeklyTaskAllotmentModel )
        {
            val showEmployeeLayout = previousName != weeklyTaskAllotmentModel.fullName
            if (showEmployeeLayout && weeklyTaskAllotmentModel.fullName!!.isNotEmpty()) {
                employeeLayout.visibility = View.VISIBLE
                employeeNameTv.text = weeklyTaskAllotmentModel.fullName
            } else {
                employeeLayout.visibility = View.GONE
            }

            val showDateLayout = previousDate != weeklyTaskAllotmentModel.allottedDate
            if (showDateLayout && weeklyTaskAllotmentModel.allottedDate!!.isNotEmpty()) {
                dateLayout.visibility = View.VISIBLE
                val splitDateParts = nonEmptyTaskDataFormatDate(weeklyTaskAllotmentModel.allottedDate!!).split(" ")
                dateTv.text = "${splitDateParts[0]}\n${splitDateParts[1]}"
            } else {
                dateLayout.visibility = View.GONE
            }

            nonEmptyDataLayout.visibility = View.VISIBLE
            projectName.text=weeklyTaskAllotmentModel.projectName
            moduleName.text=weeklyTaskAllotmentModel.moduleName
            taskNameValue.text=weeklyTaskAllotmentModel.taskName
            allotedHourValue.text= weeklyTaskAllotmentModel.allottedHours.toString()
            taskStatusValue.text=weeklyTaskAllotmentModel.taskStatus

            timeSheetValue.text = if (weeklyTaskAllotmentModel.taskStatus == "Completed") {
                "Updated"
            } else {
                "Not Updated"
            }
            workedHoursValue.text= weeklyTaskAllotmentModel.workedHours.toString()
        }

        fun nonEmptyTaskDataFormatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""
            val outputFormat = SimpleDateFormat("d EEE", Locale.getDefault())
            return outputFormat.format(date)
        }
    }
}