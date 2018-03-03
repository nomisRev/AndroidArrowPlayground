package com.github.nomisrev.androidarrowplayground.domain

import com.jakewharton.rxrelay2.Relay

/**
 * A view can represent state or preform an action.
 *
 * Side-note: actions are primarily used to preform navigation.
 * Still looking for a nicer solution. Suggestions are welcome.
 */
interface MviView<A> {
    val state: Relay<A>
}
