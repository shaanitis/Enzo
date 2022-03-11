package com.example.enzo.Models

data class AllChatsModel (var nameOfUserChatClicked: String?, var imgOfUserChatClicked:String?, var lastMessage:String?, var idOfUserChatClicked:String?){
    constructor(): this("", "", "", ""
    )
}
