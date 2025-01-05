package com.example.redcolaboracion.model

data class RequestedHelp (
    var id: String = "",
    var requestMessage: String = "",
    var requestDate: String = "",
    var category: String = "",
    var priority: String = "",
    var status: String = "",
    var efectiveHelp: String = "",
    var efectiveDate: String = "",
    var efectiveComments: String = "",
    var requestUser: String = "",
    var requestUserId: String = ""
)