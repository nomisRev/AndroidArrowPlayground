package com.github.nomisrev.androidarrowplayground

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

fun <T> Observable<T>.asRelay(): PublishRelay<T> {
    val relay = PublishRelay.create<T>()
    subscribe(relay::accept)
    return relay
}

fun View.onClick(f: (View) -> Unit) = apply {
    setOnClickListener(f)
}

fun TextView.onImeActionDone(f: (View) -> Unit) = apply {
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            f(v)
            true
        } else false
    }
}
