package com.krono.stayfit.presentation

class User {
    var uid: String = ""
    var heartRate: Double = 0.0
    //var heartRateRecordedTime: String = ""

    constructor()

    constructor(uid: String, heartRate: Double){
        this.uid = uid
        this.heartRate = heartRate
    }

}