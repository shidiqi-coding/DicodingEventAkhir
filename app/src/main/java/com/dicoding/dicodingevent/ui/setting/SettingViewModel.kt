package com.dicoding.dicodingevent.ui.setting

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _themeChoice = MutableLiveData<Int>()
    val themeChoice: LiveData<Int> = _themeChoice

    private val _isDailyReminderEnabled = MutableLiveData<Boolean>()
    val isDailyReminderEnabled: LiveData<Boolean> get() = _isDailyReminderEnabled

    init {
        _themeChoice.value = prefs.getInt("theme_choice", 0)
        _isDailyReminderEnabled.value = prefs.getBoolean("daily_reminder_enabled", false)
        applyTheme(_themeChoice.value ?: 0)
    }

    fun setThemeChoice(choice: Int) {
        _themeChoice.value = choice
        prefs.edit().putInt("theme_choice", choice).apply()
        applyTheme(choice)
    }

    private fun applyTheme(choice: Int) {
        when (choice) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun setDailyReminderState(isEnabled: Boolean) {
        _isDailyReminderEnabled.value = isEnabled
        prefs.edit().putBoolean("daily_reminder_enabled", isEnabled).apply()
    }
}
