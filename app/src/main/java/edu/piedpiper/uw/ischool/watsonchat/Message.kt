package edu.piedpiper.uw.ischool.watsonchat

class Message {
    var userName: String? = null
    var text: String? = null
    var userId: String? = null
    var time: Long? = null

    constructor() {}  // Needed for Firebase

    constructor(text: String, userId: String, userName: String, time:Long) {
        this.userName = userName
        this.text = text
        this.userId = userId
        this.time = time
    }
}