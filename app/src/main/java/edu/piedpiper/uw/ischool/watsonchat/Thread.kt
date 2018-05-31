package edu.piedpiper.uw.ischool.watsonchat

import com.google.firebase.database.IgnoreExtraProperties

class Thread {
    var lastMessageTime: Long? = null
    var threadName: String? = null
    var lastMessageText: String? = null
    var threadId: String? = null

    constructor() {}  // Needed for Firebase

    constructor(lastMessageTime:Long, threadName:String, threadId:String, lastMessageText:String) {
        this.lastMessageTime = lastMessageTime
        this.threadName = threadName
        this.lastMessageText = lastMessageText
        this.threadId = threadId
    }
}