package com.example.redcolaboracion.model

data class RequestedHelp (
    var requestMessage: String = "",
    var requestDate: String = "",
    var priority: String = "",
    var efectiveHelp: String = "",
    var efectiveDate: String = "",
    var uidCategory: String = "",
    var uidUser: String = "",
    var uidStatus: String = ""
)