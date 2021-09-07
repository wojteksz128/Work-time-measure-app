package net.wojteksz128.worktimemeasureapp.util.livedata

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData

class ObservableLiveData<T>(value: T? = null) : MutableLiveData<T>(value) where T : BaseObservable {

    private val callback = object : Observable.OnPropertyChangedCallback() {

        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            setValue(getValue())
        }
    }

    override fun setValue(value: T?) {
        super.setValue(value)

        value?.addOnPropertyChangedCallback(callback)
    }
}