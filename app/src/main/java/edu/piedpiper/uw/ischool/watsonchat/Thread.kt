package edu.piedpiper.uw.ischool.watsonchat

import com.google.firebase.database.IgnoreExtraProperties

class Thread {
    var timeCreated: Long? = null
    var threadName: String? = null
    var threadId: String? = null

    constructor() {}  // Needed for Firebase

    constructor(timeCreated:Long, threadName:String, threadId:String) {
        this.timeCreated = timeCreated
        this.threadName = threadName
        this.threadId = threadId
    }
}