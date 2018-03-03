package com.github.nomisrev.androidarrowplayground.domain.tictactoe

import arrow.core.Option
import arrow.core.monad
import arrow.data.ListK
import arrow.data.k
import arrow.syntax.monad.flatten
import arrow.syntax.option.toOption

typealias Board = ListK<ListK<Option<Stone>>>
val Board.width: Int get() = size
val Board.height: Int get() = getOrNull(0)?.size ?: 0

operator fun Board.get(pos: Pos): Option<Stone> = getOrNull(pos.x).toOption().flatMap {
    it.getOrNull(pos.y).toOption().flatten(Option.monad())
}

fun Board.updated(pos: Pos, stone: Option<Stone>): Board =
        updated(pos.x, get(pos.x).updated(pos.y, stone))

fun <A> ListK.Companion.fill(n: Int, a: A): ListK<A> = (0 until n).map { a }.k()

fun <A> ListK<A>.updated(i: Int, newA: A): ListK<A> = updateModify(i) { _ -> newA }

fun <A> ListK<A>.updateModify(i: Int, f: (A) -> A): ListK<A> = mapIndexed { index, a -> if (index == i) f(a) else a }.k()
