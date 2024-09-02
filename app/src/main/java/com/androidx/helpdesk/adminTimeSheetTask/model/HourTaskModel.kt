package com.androidx.helpdesk.adminTimeSheetTask.model

data class HourTaskModel(
    var empName:String?,
    var projectName: String?,
    var moduleName: String?,
    var taskName: String?,
    var taskStatus: String?,
    var datePart: String?,
    var allottedHours: Int?

)