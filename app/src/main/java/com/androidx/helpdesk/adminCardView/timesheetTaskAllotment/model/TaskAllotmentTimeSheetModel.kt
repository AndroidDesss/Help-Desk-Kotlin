package com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model

data class EmployeeTaskAllotmentModel(
    var empId: Int?,
    var fullName: String?,
    var depId: Int?,
    var department: String?,
    var departmentData: List<TaskAllotmentTimeSheetModel>
)

class TaskAllotmentTimeSheetModel(
    var depId: Int?,
    var department: String?,
    var taskRowCount: Int?,
    var timesheetRowCount: Int?,
    var projectTaskId: Int?,
    var timeSheetId: Int?,
    var projectName: String?,
    var moduleName: String?,
    var taskName: String?,
    var taskStatus: String?,
    var allottedHours: Int?,
    var timesheetAllottedHours: Int?,
    var actualWorkedHours: Int?
)
