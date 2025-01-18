package com.app.leaflet

data class SessionPlan(
    val className: String,
    val classSpecialty: String,
    val classLevel: String,
    val classYear: String,
    val groupName: String,
    val groupType: String,
    val day: String,
    val time: String,
    val groupId: Int,
    val planerId: Int
)
