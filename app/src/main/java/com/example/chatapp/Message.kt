package com.example.chatapp

import java.util.Date


class Message {
    var message: String? = null
    var senderId: String? = null
    lateinit var timeStamp: Date

    constructor(){}

    constructor(message: String?, senderId: String?, timeStamp: Date){
        this.message = message
        this.senderId = senderId
        this.timeStamp = timeStamp
    }
}