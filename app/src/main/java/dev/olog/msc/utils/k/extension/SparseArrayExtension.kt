package dev.olog.msc.utils.k.extension

import android.util.LongSparseArray
import androidx.util.forEach

fun <T> LongSparseArray<T>.toList(): List<T>{
    val list = mutableListOf<T>()

    this.forEach { _, value -> list.add(value) }

    return list
}

fun <T> LongSparseArray<T>.toggle(key: Long, item: T){
    val current = this.get(key)
    if (current == null){
        this.put(key, item)
    } else {
        this.remove(key)
    }
}