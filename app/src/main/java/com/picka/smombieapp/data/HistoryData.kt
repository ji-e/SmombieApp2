package com.picka.smombieapp.data

data class HistoryData(
    val date:String?,
    val time: String?,
    val step: String?,
    val endCnt: String?,
    val sub: MutableList<HistoryData2>?
)

 class HistoryData2(
    val date: String?,
    val start: String?,
    val end: String?,
    val time: String?,
    val step: String?
)