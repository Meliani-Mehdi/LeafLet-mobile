package com.app.leaflet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class stAViewModel : ViewModel() {
    private val _studentCount = MutableLiveData(0)
    val studentCount: LiveData<Int> get() = _studentCount

    fun updateStudentCount(count: Int) {
        _studentCount.value = count
    }
}
