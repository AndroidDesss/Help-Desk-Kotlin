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

    var getModule = "http://helpdeskservice.desss-portfolio.com/api/ModuleByProject/LoadModule?CompanyID="

    var getPhase = "https://helpdeskservice.desss-portfolio.com/api/Phase/Loadphase"

    var getTaskCategory = "https://helpdeskservice.desss-portfolio.com/api/SubModTaskCat/GetTaskCategory?CompanyID="

    var getIssueType = "https://helpdeskservice.desss-portfolio.com/api/SubModIssueType/GetIssueType"

    var getEstimateModule = "http://helpdeskservice.desss-portfolio.com/api/GetModule/GetModule?CompanyID="

    var getEstimateScreenName = "http://helpdeskservice.desss-portfolio.com/api/GetScreenName/DropDownScreenName?CompanyID="

    var deleteModuleById = "http://helpdeskservice.desss-portfolio.com/api/DeleteModule/DeleteRow?ModuleID="

    var editModuleById = "https://helpdeskservice.desss-portfolio.com/api/EditModule/Edit?CompanyID="

    var updateModule = "http://helpdeskservice.desss-portfolio.com/api/UpdateModule/Update?ProjectID="

    var addSubModule = "https://helpdeskservice.desss-portfolio.com/api/InsertSubModule/InsertData?CompanyID="

    var updateSubModule = "https://helpdeskservice.desss-portfolio.com/api/UpdateSubModule/ProjectTaskUpdate?CompanyID="

    var deleteSubModuleById = "https://helpdeskservice.desss-portfolio.com/api/DeleteSubModule/Grid_RowDelete?PrjTaskID="

    var editSubModuleById = "https://helpdeskservice.desss-portfolio.com/api/EditSubModule/Edit?CompanyID="

    var getEstimateAllotProjectTaskId = "http://helpdeskservice.desss-portfolio.com/api/SearchButton/GetprojectTaskBySearch?module="

    var getEstimateAllotTaskList = " https://helpdeskservice.desss-portfolio.com/api/LoadTaskName/LoadGrid?TaskCategory="

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

    var getBackLogList = "https://helpdeskservice.desss-portfolio.com/api/BackLogList/LoadGrid?CompanyID="

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
}