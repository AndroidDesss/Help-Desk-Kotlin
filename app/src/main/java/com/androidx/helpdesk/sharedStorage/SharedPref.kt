package com.androidx.helpdesk.sharedStorage

import android.content.Context

object SharedPref {
    fun setCompanyId(context: Context, companyId: String?) {
        SharedPrefUtils.putString(context, "companyId", companyId)
    }

    fun getCompanyId(context: Context?): String? {
        return SharedPrefUtils.getString(context, "companyId")
    }

    fun setEmployeeId(context: Context, employeeId: Int) {
        SharedPrefUtils.putInt(context, "employeeId", employeeId)
    }

    fun getEmployeeId(context: Context?): Int {
        return SharedPrefUtils.getInt(context, "employeeId")
    }

    fun setUserLoggedIn(context: Context, loggedInStatus: Boolean) {
        SharedPrefUtils.putBoolean(context, "userLoggedIn", loggedInStatus)
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return SharedPrefUtils.getBoolean(context, "userLoggedIn")
    }

    fun setUserType(context: Context, userType: String?) {
        SharedPrefUtils.putString(context, "userType", userType)
    }

    fun getUserType(context: Context?): String? {
        return SharedPrefUtils.getString(context, "userType")
    }

    fun setUserId(context: Context, userId: String?) {
        SharedPrefUtils.putString(context, "userId", userId)
    }

    fun getUserId(context: Context?): String? {
        return SharedPrefUtils.getString(context, "userId")
    }

    fun setFirstName(context: Context, firstName: String?) {
        SharedPrefUtils.putString(context, "firstName", firstName)
    }

    fun getFirstName(context: Context?): String? {
        return SharedPrefUtils.getString(context, "firstName")
    }

    fun setLastName(context: Context, lastName: String?) {
        SharedPrefUtils.putString(context, "lastName", lastName)
    }

    fun getLastName(context: Context?): String? {
        return SharedPrefUtils.getString(context, "lastName")
    }

    fun setClientId(context: Context, clientId: Int) {
        SharedPrefUtils.putInt(context, "clientId", clientId)
    }

    fun getClientId(context: Context?): Int {
        return SharedPrefUtils.getInt(context, "clientId")
    }

    fun setClientName(context: Context, clientName: String?) {
        SharedPrefUtils.putString(context, "clientName", clientName)
    }

    fun getClientName(context: Context?): String? {
        return SharedPrefUtils.getString(context, "clientName")
    }

    fun setDepartmentId(context: Context, departmentId: Int) {
        SharedPrefUtils.putInt(context, "departmentId", departmentId)
    }

    fun getDepartmentId(context: Context?): Int {
        return SharedPrefUtils.getInt(context, "departmentId")
    }

    fun setUserName(context: Context, userName: String) {
        SharedPrefUtils.putString(context, "userName", userName)
    }

    fun getUserName(context: Context?): String? {
        return SharedPrefUtils.getString(context, "userName")
    }

    fun setScreenId(context: Context, screenId: Int) {
        SharedPrefUtils.putInt(context, "screenId", screenId)
    }

    fun getScreenId(context: Context?): Int {
        return SharedPrefUtils.getInt(context, "screenId")
    }
}