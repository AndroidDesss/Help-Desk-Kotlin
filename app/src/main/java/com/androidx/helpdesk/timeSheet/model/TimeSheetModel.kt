package com.androidx.helpdesk.timeSheet.model

class TimeSheetModel(
    var timeSheetProjectId: Int,
    var timeSheetProjectName: String?,
    var timeSheetTaskName: String?,
    var timeSheetAllottedTo: String?,
    var timeSheetAssignedDate: String?,
    var timeSheetAllottedHours: Int,
    var taskDescription: String?
)