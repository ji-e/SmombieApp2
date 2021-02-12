package com.picka.smombieapp.service

interface StepCallback {
    fun onStepCallback(step:String)
    fun onUnbindService()
    fun onEndService()
}
