package com.androidx.helpdesk.apilist

object Api {

    var login = "http://helpdeskservice.desss-portfolio.com/api/Login/LoginAdmin?CompanyID="

    var openTickets = "http://helpdeskservice.desss-portfolio.com/api/Login/GetTicketsDetails?UserTypeID="

    var closedTickets = "http://helpdeskservice.desss-portfolio.com/api/ClosedTickets/GetTicketsClosedDetails?UserTypeID="

    var progressTickets = "http://helpdeskservice.desss-portfolio.com/api/ProgressTickets/GetTicketsProgressDetails?UserTypeID="

    var completedTickets = "https://helpdeskservice.desss-portfolio.com/api/CompletedTicket/GetTicketsCompletedDetails?UserTypeID="

    var projectList = "http://helpdeskservice.desss-portfolio.com/api/ClientCRUD/GetProjectNamebyEmpID?CompanyID="

    var taskTypeList = "http://helpdeskservice.desss-portfolio.com/api/ClientCRUD/GettblLookupModuleTypes"

    var emailList = "http://helpdeskservice.desss-portfolio.com/api/ClientCRUD/GetEmailTobyProjectName?ProjectID="

    var createTaskForClient = "http://helpdeskservice.desss-portfolio.com/api/ClientCRUD/InsertTicketbyEmp?EmpID="

    var getClientProfile = "http://helpdeskservice.desss-portfolio.com/api/ClientProfile/GetClientprofile?ClientID="

    var updateProfile = "http://helpdeskservice.desss-portfolio.com/api/ClientProfile/UpdateClientprofile?ClientID="

    var getTicketDetails = "http://helpdeskservice.desss-portfolio.com/api/ClientCRUD/GetTicketbyMiscEID?MiscEID="

    var deleteTicketDetails = "http://helpdeskservice.desss-portfolio.com/api/ProgressTickets/DeleteTicketbyid?MiscEID="

    var updateTicketDetails = "http://helpdeskservice.desss-portfolio.com/api/ProgressTickets/UpdateTicketbyEmp?MiscEID="

    var getTimeSheetList = "http://helpdeskservice.desss-portfolio.com/api/ClientProfile/GetProject_task?ProjectName="

    var projectListByUser = "http://helpdeskservice.desss-portfolio.com/api/Lode_tblProjectModelodeByproject/proc_tblProjectModuleLoadByProject?CompanyID="

    var moduleList = "http://helpdeskservice.desss-portfolio.com/api/ModelFrom_project/Modulefromproject?ProjectID="

    var taskCategoryList = "http://helpdeskservice.desss-portfolio.com/api/TaskCategorys/Taskcategory?CompanyID="

    var assignedTo = "http://helpdeskservice.desss-portfolio.com/api/AllotedToDropdown/AllotedTO?CompanyID="

    var getTaskStatus = "https://helpdeskservice.desss-portfolio.com/api/TaskStatus/LodeTaskStatus"

    var getTimeSheetDetails = "http://helpdeskservice.desss-portfolio.com/api/ClientProfile/GettblProjectTaskAllotment1?PrjTaskAllotID="

    var getTask = "http://helpdeskservice.desss-portfolio.com/api/TaskDropdown/loadTask?TaskCategory="

    var getToolsList = "https://helpdeskservice.desss-portfolio.com/api/proc_LoadToolsByEmp12/loadtool?CompanyID="

    var updateTimeSheetDetails = "https://helpdeskservice.desss-portfolio.com/api/Login/getEmpTask?TaskAllotmentID="

    var getChatList = "https://helpdeskservice.desss-portfolio.com/api/clientsheet/GetFilestatus?TicketId="

    var postChatMessage = "https://helpdeskservice.desss-portfolio.com/api/clientsheet/Insertchat?Message="

    var postFeedBack = "https://helpdeskservice.desss-portfolio.com/api/TicketSave/Saverating?Stars="

    var closeTickets = "https://helpdeskservice.desss-portfolio.com/api/TicketStatusUpd/Ticket?ticketid="

    var getUserNameEmail = "https://helpdeskservice.desss-portfolio.com/api/TicketSave/Getrating?ClientUserID="

    var reOpenTickets = "https://helpdeskservice.desss-portfolio.com/api/ReopenTicket/UpdateticketStatus?MiscEID="

    var adminProjectList = "https://helpdeskservice.desss-portfolio.com/api/ProjectList/LoadGrid?UserTypeID="

    var createQuickProject = "https://helpdeskservice.desss-portfolio.com/api/QuickProject/CreateQuickProject?CompanyID="

    var getAssignedProjectMembers = "http://helpdeskservice.desss-portfolio.com/api/ProjectMembers/LoadEmp?CompanyID="

    var getAvailableProgrammers = "https://helpdeskservice.desss-portfolio.com/api/AssignResources/LoadEmpoyee?CompanyID="

    var addAvailableProgrammers = "https://helpdeskservice.desss-portfolio.com/api/AssignResources/AddEmployee?CompanyID="

    var getModuleList = "https://helpdeskservice.desss-portfolio.com/api/ModuleList/LoadGrid?CompanyID="

    var getSubModuleList = "http://helpdeskservice.desss-portfolio.com/api/LoadSubModule/LoadGrid?CompanyID="

    var addModule = "http://helpdeskservice.desss-portfolio.com/api/SaveModuleData/SaveModule?StartDate="

    var getEstimateModule = "http://helpdeskservice.desss-portfolio.com/api/GetModule/GetModule?CompanyID="

    var getEstimateScreenName = "http://helpdeskservice.desss-portfolio.com/api/GetScreenName/DropDownScreenName?CompanyID="

    var deleteModuleById = "http://helpdeskservice.desss-portfolio.com/api/DeleteModule/DeleteRow?ModuleID="

    var editModuleById = "https://helpdeskservice.desss-portfolio.com/api/EditModule/Edit?CompanyID="

    var updateModule = "http://helpdeskservice.desss-portfolio.com/api/UpdateModule/Update?ProjectID="

    var addSubModule = "https://helpdeskservice.desss-portfolio.com/api/InsertSubModule/InsertData?CompanyID="

    var getEstimateAllotProjectTaskId = "http://helpdeskservice.desss-portfolio.com/api/SearchButton/GetprojectTaskBySearch?module="

    var getEstimateAllotTaskList = "https://helpdeskservice.desss-portfolio.com/api/LoadTaskName/LoadGrid?TaskCategory="

    var addTaskEstimateAllot = "https://helpdeskservice.desss-portfolio.com/api/AddTaskName/AddTask?taskId="

    var getGridListValues = "http://helpdeskservice.desss-portfolio.com/api/GridallotProjects/LoadGrid?ProjectID="

    var getBoardListValues = "http://helpdeskservice.desss-portfolio.com/api/Board/LoadBoard?ProjectID="

    var getAssignedByList = "http://helpdeskservice.desss-portfolio.com/api/AssignedBy/loadEmp?CompanyID="

    var getAssignedToList = "http://helpdeskservice.desss-portfolio.com/api/AssignedBy/loadEmp?CompanyID="

    var getTaskType = "http://helpdeskservice.desss-portfolio.com/api/ProjectTaskType/LoadTasktype"

    var getEstimateAllotTaskStatus = "http://helpdeskservice.desss-portfolio.com/api/TaskStatus/LodeTaskStatus"

    var deleteEstimateAllotGrid = "http://helpdeskservice.desss-portfolio.com/api/DeleteGridAllot/DeleteRow?PrjTaskAllotID="

    var pushToTaskAllotment = "http://helpdeskservice.desss-portfolio.com/api/PushToTaskAllotment/LoadGrid?PrjTaskAllotID="

    var taskDuplicate = "http://helpdeskservice.desss-portfolio.com/api/DuplicateButton/Duplicate?PrjTaskAlotID="

    var getSprintList = "http://helpdeskservice.desss-portfolio.com/api/GridSprint/LoadBoard?ProjectID="

    var editGridAllotEdit = "http://helpdeskservice.desss-portfolio.com/api/GridAllotProjectEdit/LoadGrid?ProjectID="

    var updateAllotGrid = "http://helpdeskservice.desss-portfolio.com/api/GridAllotProjectUpdate/UpdateGridAllot?PrjTaskAllotID="

    var editProjectDetails = "http://helpdeskservice.desss-portfolio.com/api/QuickEdit/LoadAll?ProjectID="
    
    var updateProjectName = "http://helpdeskservice.desss-portfolio.com/api/UpdateQuickEdit/Update?CompanyID="

    var deleteProjectById = "http://helpdeskservice.desss-portfolio.com/api/DeleteProjectinList/Delete?ProjectID="

    var deleteAssignResourcesById = "http://helpdeskservice.desss-portfolio.com/api/RemoveButton/DeleteRow?PrjEmpID="

    var getBackLogProjectList = "https://helpdeskservice.desss-portfolio.com/api/GetProjectName/GetProjectName?CompanyID="

    var getBackLogIssueType = "https://helpdeskservice.desss-portfolio.com/api/ProjectTaskType/LoadTasktype"

    var getBackLogWorkFlow = "https://helpdeskservice.desss-portfolio.com/api/SubModTaskCat/GetTaskCategory?CompanyID="

    var getBackLogReportedType = "https://helpdeskservice.desss-portfolio.com/api/AssignedBy/loadall?CompanyID="

    var getBackLogModuleType = "https://helpdeskservice.desss-portfolio.com/api/ModuleList/LoadGrid?CompanyID="

    var getBackLogBoardType = "https://helpdeskservice.desss-portfolio.com/api/Board/LoadBoard?ProjectID="

    var getBackLogSprintType = "https://helpdeskservice.desss-portfolio.com/api/GridSprint/LoadBoard?ProjectID="

    var insertNewBackLog = "https://helpdeskservice.desss-portfolio.com/api/InsertBackLog/Insert"

    var getBackLogDetailsById = "https://helpdeskservice.desss-portfolio.com/api/AllotedTaskList/Getboarddeatilsbyprjid?PrjTaskID="

    var deleteBackLog = "https://helpdeskservice.desss-portfolio.com/api/DeleteSubModule/Grid_RowDelete?PrjTaskID="

    var updateBackLog = "https://helpdeskservice.desss-portfolio.com/api/UpdateBackLog/Update"

    var editSprint = "http://helpdeskservice.desss-portfolio.com/api/EditSprintData/LoadSprintGrid?ProjectID&UserTypeID="

    var createSprint = "http://helpdeskservice.desss-portfolio.com/api/InsertSprintData/InsertData?ProjectID="

    var updateSprint = "http://helpdeskservice.desss-portfolio.com/api/UpdateSprintData/Update?ProjectID="

    var getCurrentSprintList = "http://helpdeskservice.desss-portfolio.com/api/GetSprintData/LoadSprintGrid?ProjectID&UserTypeID="

    var deleteSprint = "http://helpdeskservice.desss-portfolio.com/api/DeleteSprintData/Delete?SprintID="

    var unAssignedBackLogList = "https://helpdeskservice.desss-portfolio.com/api/BackLogList/LoadGrid?CompanyID="

    var assignedBackLogList = "https://helpdeskservice.desss-portfolio.com/api/BackLogList/LoadGridAssigned?CompanyID="

    var inProgressBackLogList = "https://helpdeskservice.desss-portfolio.com/api/BackLogList/LoadGridInProgress?CompanyID="

    var completedBackLogList = "https://helpdeskservice.desss-portfolio.com/api/BackLogList/LoadGridCompleted?CompanyID="

    var getPendingSprintList = "http://helpdeskservice.desss-portfolio.com/api/GetPendingSprintData/LoadPendingSprintGrid?ProjectID=&UserTypeID="

    var getBackLogTaskList = "https://helpdeskservice.desss-portfolio.com/api/LoadTaskName/LoadGrid?TaskCategory="

    var getPendingTaskSprintList = "http://helpdeskservice.desss-portfolio.com/api/GetPendingSprintData/LoadPendingTaskGrid?ProjectID="

    var sprintDetailsList = "https://helpdeskservice.desss-portfolio.com/api/GetSprintData/LoadSprintDetails?SprintID="

    var deleteSprintDetails = "https://helpdeskservice.desss-portfolio.com/api/DeleteSprintDetails/Delete?SprintID="

    var addPendingTask = "https://helpdeskservice.desss-portfolio.com/api/AddSprint/AddsprintButton?PrjTaskAllotID="

    var getBoardList = "https://helpdeskservice.desss-portfolio.com/api/GetBoard/LoadGrid"

    var boardDelete = "https://helpdeskservice.desss-portfolio.com/api/DeleteBoard/Delete?BoardID="

    var editBoardDetails = "https://helpdeskservice.desss-portfolio.com/api/EditBoard/Edit?BoardID="

    var updateBoardDetails = "https://helpdeskservice.desss-portfolio.com/api/UpdateBoard/Update?BoardID="

    var insertNewBoard = "https://helpdeskservice.desss-portfolio.com/api/InsertBoard/InsertData?ProjectID="

    var getBoardMembersList = "https://helpdeskservice.desss-portfolio.com/api/BoardMembersList/LoadGrid?BoardID="

    var deleteBoardMembers = "http://helpdeskservice.desss-portfolio.com/api/DeleteBoardMembers/Delete?BoardMemberID="

    var getAvailableBoardProjectMembers = "https://helpdeskservice.desss-portfolio.com/api/ProjectMembersList/LoadGrid?ProjectID="

    var addAvailableBoardMembers = "http://helpdeskservice.desss-portfolio.com/api/AddBoard/Add_button?EmpID="

    var burnOutChart = "https://helpdeskservice.desss-portfolio.com/api/BurnOutChart/LoadBurnOutChart?SprintID="

    var getDeportment = "http://helpdeskservice.desss-portfolio.com/api/DeptDropdown/LoadDeptDropdown"

    var getNoTaskDetails = "http://helpdeskservice.desss-portfolio.com/api/TodaysTask/LoadNotTaskAlloted?selecteddate="

    var getHourTaskDetails = "http://helpdeskservice.desss-portfolio.com/api/TodaysTask/LoadTaskLessHour?selecteddate="

    var getHaveTaskDetails ="http://helpdeskservice.desss-portfolio.com/api/TodaysTask/LoadTaskList?TaskDate="

    var getNotUpdatedTimeSheetDetails = "http://helpdeskservice.desss-portfolio.com/api/CVTimeSheetList/LoadTimeSheetNotUpdated?selecteddate="

    var updatedTimeSheetDetails = "http://helpdeskservice.desss-portfolio.com/api/CVTimeSheetList/LoadTimeSheetUpdated?TaskDate="

    var getUnAssignedSummary = "http://helpdeskservice.desss-portfolio.com/api/CardViewUnassigned/LoadUnassigned?user_projectname="

    var getAssignedSummary = "http://helpdeskservice.desss-portfolio.com/api/Cardview_Assigned/GetCVAssigndList?EmpID="

    var getCompletedSummary = "http://helpdeskservice.desss-portfolio.com/api/Cardview_Completed/GetCVCompletedList?EmpID="

    var getWeeklyAllotmentList = "https://helpdeskservice.desss-portfolio.com/api/WeeklyTaskList/GetWeeklyTaskList?"

//    var taskAllotmentTimesheetList = "http://helpdeskservice.desss-portfolio.com/api/Cardview_TaskAllotmentVsTimeSheetList/GetCVTaskAllotmentVsTimeSheetList?Taskdate="

    var taskAllotmentTimeSheetList = "https://helpdeskservice.desss-portfolio.com/api/Cardview_TaskAllotmentVsTimeSheetList/GetTaskAllotVsTimeSheet?Taskdate="

    var backLogTaskAllottedList = "https://helpdeskservice.desss-portfolio.com/api/AllotedTaskList/LoadGrid?PrjTaskID="

    var backLogTaskTimeSheetList = "https://helpdeskservice.desss-portfolio.com/api/TimeSheetList/LoadGrid?PrjTaskAllotId="
}