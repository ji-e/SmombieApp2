package com.example.smombieapp.utils

import android.app.Activity
import android.content.Context
import com.example.smombieapp.base.BaseApplication.Companion.useSharedPreference

/**
 * create by jieun
 */
class UseSharedPreferences(mContext: Context) {

    private var key = "key"
    private var preferences = mContext.getSharedPreferences(mContext.packageName, Activity.MODE_PRIVATE)

    var data: String
        get() = preferences.getString(key, "") ?: ""
        set(value) = preferences.edit().putString(key, value).apply()

    /**
     * 프리퍼런스 키 값 세팅
     * @key: String
     */
    fun setKey(key: String) {
        this.key = key
    }



    /**
     * 프리퍼런스 값 가져오기
     * @key:String
     */
    fun getPreferencesData(key: String): String {
        val preferences = this.apply {
            setKey(key)
        }
        return preferences.data
    }

    /**
     * 프리퍼런스 값 설정하기
     * @key:String
     * @value:String
     */
    fun setPreferencesData(key: String, value: String) {
        val preferences = this.apply {
            setKey(key)
        }
        preferences.data = value
    }

    /**
     * 프리퍼런스 값 제거하기
     * @key:String
     */
    fun removePreferencesData(key: String) {
        val preferences = this.apply {
            setKey(key)
        }
        preferences.data = ""
    }

}