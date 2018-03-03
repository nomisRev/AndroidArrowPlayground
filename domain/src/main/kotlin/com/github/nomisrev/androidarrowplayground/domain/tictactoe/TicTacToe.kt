package com.github.nomisrev.androidarrowplayground.domain.tictactoe

import arrow.Kind
import arrow.core.Option
import arrow.syntax.option.none
import arrow.syntax.option.some
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding

/**
 * Tic tac toe algebra.
 */
interface TicTacToe<F> {

    fun monadError(): MonadError<F, TicTacToeError>

    /** Reset the game. */
    fun reset(): Kind<F, Unit>

    /** Place a [Stone] at [Pos]. */
    fun place(pos: Pos): Kind<F, Unit>

    /** Check if [Stone] won the game. */
    fun win(stone: Stone): Kind<F, Boolean>

    /** Check which [Stone] is at [Pos]. */
    fun on(pos: Pos): Kind<F, Option<Stone>>

    /** Check whose turn it is. */
    fun turn(): Kind<F, Option<Stone>>

    /** Check who the winner is. */
    fun winner(): Kind<F, Option<Stone>> = monadError().binding {
        val didXWin = win(Stone.X).bind()
        val didOWin = win(Stone.O).bind()
        if (didXWin) Stone.X.some() else if (didOWin) Stone.O.some() else none()
    }

    /** Check if the current turn is [Stone]. */
    fun currentTurnIs(stone: Stone): Kind<F, Boolean> =
            monadError().map(turn()) { it.exists { it == stone } }

}

sealed class Stone(val symbol: String) {
    object O : Stone(symbol = "O")
    object X : Stone(symbol = "X")

    val opponent = when (this) {
        is Stone.X -> Stone.O
        is Stone.O -> Stone.X
    }
}

data class Pos(val x: Int, val y: Int)

sealed class TicTacToeError
data class OccupiedPosition(val position: Pos) : TicTacToeError()
data class NotInTheBoard(val position: Pos) : TicTacToeError()
