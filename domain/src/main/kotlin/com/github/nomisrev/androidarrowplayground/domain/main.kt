package com.github.nomisrev.androidarrowplayground.domain

import arrow.core.EitherPartialOf
import arrow.data.StateTPartialOf
import arrow.mtl.monadState
import com.github.nomisrev.androidarrowplayground.domain.tictactoe.TicTacToeError
import com.github.nomisrev.androidarrowplayground.domain.tictactoe.TicTacToeState

fun main(args: Array<String>) {
    monadState<EitherPartialOf<TicTacToeError>, TicTacToeState>()
            .let(::println)
}