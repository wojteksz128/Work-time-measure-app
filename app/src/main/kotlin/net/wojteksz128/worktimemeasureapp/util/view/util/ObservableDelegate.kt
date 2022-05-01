package net.wojteksz128.worktimemeasureapp.util.view.util

import android.util.Log
import androidx.databinding.BaseObservable
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import kotlin.reflect.KProperty

open class ObservableDelegate<T>(
    val id: Int,
    initialValue: T,
    private val onChanged: (T) -> Unit = {}
) : ClassTagAware {

    var value = initialValue

    operator fun getValue(self: BaseObservable, prop: KProperty<*>): T {
        Log.d(classTag, "getValue: Get value for ${prop.name}")
        return value
    }

    operator fun setValue(self: BaseObservable, prop: KProperty<*>, value: T) {
        this.value = value
        onChanged.invoke(value)
        self.notifyPropertyChanged(id)
    }
}