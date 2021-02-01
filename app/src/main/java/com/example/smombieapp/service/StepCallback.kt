package com.example.smombieapp.service

interface StepCallback {
    fun onStepCallback(step:String)
    fun onUnbindService()
    fun onStopService()
}