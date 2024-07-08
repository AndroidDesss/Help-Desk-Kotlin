package com.androidx.helpdesk.adminCardView.timesheet.model

class UpdatedTimeSheetModel(
    var timeSheetId: Int?,
    val projectName: String?,
    val moduleName: String?,
    var taskName: String?,
    var allottedHours: Int?,
    var workingHours: Int?,
    var taskStatus: String?,
    var notes: String?
)