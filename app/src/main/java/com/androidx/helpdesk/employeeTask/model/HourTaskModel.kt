package com.androidx.helpdesk.employeeTask.model

data class HourTaskModel(
    var projectName: String?,
    var moduleName: String?,
    var taskName: String?,
    var taskStatus: String?,
    var datePart: String?,
    var allottedHours: Int?

)