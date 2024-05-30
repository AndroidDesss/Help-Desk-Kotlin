package com.androidx.helpdesk.sprint.model

class SprintDetailsModel (
    var sprintListDetailsId: Int,
    var sprintId: Int,
    var taskName: String?,
    var projectName: String?,
    var description: String?,
    var empName : String?,
    var allottedHours: Double?,
    var actualHours: Double?,
    var taskDate: String?
)