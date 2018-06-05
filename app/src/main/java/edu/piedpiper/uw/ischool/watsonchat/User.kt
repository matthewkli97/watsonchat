package edu.piedpiper.uw.ischool.watsonchat

class User {
    var uid: String? = null
    var uName: String? = null
    var selected: Boolean = false

    constructor() {}  // Needed for Firebase

    constructor(uid: String, uName: String, selected:Boolean) {
        this.uid = uid
        this.uName = uName
        this.selected = selected
    }
}