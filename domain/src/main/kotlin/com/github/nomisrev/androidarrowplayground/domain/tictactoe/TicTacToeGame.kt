package com.github.nomisrev.androidarrowplayground.domain.tictactoe

import arrow.core.*
import arrow.data.*
import arrow.mtl.MonadStateSyntax
import arrow.mtl.bindingFilter
import arrow.syntax.collections.prependTo
import arrow.syntax.collections.tail
import arrow.syntax.foldable.find
import arrow.syntax.monad.flatten
import arrow.syntax.monad.ifM
import arrow.syntax.option.none
import arrow.syntax.option.some
import arrow.typeclasses.*
import javax.inject.Inject

data class TicTacToeState(val board: Board, val turn: Stone)

/**
 * [TicTacToe] implemented using [StateT].
 */
class TicTacToeGame @Inject constructor() : MonadErrorSyntax<StateTPartialOf<EitherPartialOf<TicTacToeError>, TicTacToeState>, TicTacToeError>,
        TicTacToe<StateTPartialOf<EitherPartialOf<TicTacToeError>, TicTacToeState>> {

    companion object {
        val winCount: Int = 3
        val empty: TicTacToeState = TicTacToeState(
                ListK.fill(3, ListK.fill(3, none())),
                Stone.X
        )
    }

    override fun monadError(): MonadError<StateTPartialOf<EitherPartialOf<TicTacToeError>, TicTacToeState>, TicTacToeError> = StateT.monadError(Either.monadError(), Unit)

    override fun reset(): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = StateT.set(Either.monadError(), empty)

    override fun place(pos: Pos): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = monadError().binding {
        checkOutsideBoard(pos).bind()
        val stone = currentTurnIs(Stone.X).bind().let { isX -> if (isX) Stone.X else Stone.O }
        checkOccupied(pos).bind()
        setStone(pos, stone).bind()
        setTurn(stone.opponent).bind()
    }.fix()

    override fun win(stone: Stone): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Boolean> = StateT.inspect(Either.monadError()) { state ->
        ListK.monadFilter().bindingFilter {
            val x = (0..state.board.width).toList().k().bind()
            val y = (0..state.board.height).toList().k().bind()
            val pos = Pos(x, y)
            val xyStone = state.board[pos]
            xyStone.fold(
                    { none<Stone>() },
                    { if (state.board.won(it, pos)) it.some() else none() }
            )
        }.find { it.isDefined() }.flatten().fix().exists { it == stone }
    }

    override fun on(pos: Pos): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Option<Stone>> = monadError().binding {
        checkOutsideBoard(pos).bind()
        StateT.inspect(Either.monadError<TicTacToeError>()) { state: TicTacToeState ->
            state.board[pos]
        }.bind()
    }.fix()

    override fun turn(): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Option<Stone>> = monadError().ifM(
            gameInProgress(),
            { StateT.inspect(Either.monadError<TicTacToeError>()) { state: TicTacToeState -> state.turn.some() } },
            { none<Stone>().pure() }
    ).fix()

    /** Check if [Pos] is outside playing board by modifying the state with a function that returns `Either<TicTacToeError, A>`. modifyF with Either.applicative() */
    private fun checkOutsideBoard(pos: Pos): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = StateT.modifyF(Either.applicative()) { state ->
        if (pos.x in 0 until state.board.width && pos.y in 0 until state.board.height) Either.monadError<TicTacToeError>().pure(state)
        else Either.monadError<TicTacToeError>().raiseError(NotInTheBoard(pos))
    }

    private fun checkOccupied(pos: Pos): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = monadError().ifM(
            on(pos).map { it.isEmpty() },
            { Unit.pure() },
            { OccupiedPosition(pos).raiseError() }
    ).fix()

    /** Modify state with function to place [Stone] at [Pos]. */
    private fun setStone(pos: Pos, stone: Stone): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = StateT.modify(Either.applicative()) { state ->
        state.copy(board = state.board.updated(pos, stone.some()))
    }

    /** Modify state with function to set turn [Stone]. */
    private fun setTurn(turn: Stone): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Unit> = StateT.modify(Either.applicative()) { state ->
        state.copy(turn = turn)
    }

    private fun gameInProgress(): StateT<EitherPartialOf<TicTacToeError>, TicTacToeState, Boolean> =
            winner().map { it.isEmpty() }.fix()

    /** Check the if [horMoves], [vertMoves], [diagRightMoves] or [diagLeftMoves] contains a winning sequence. */
    private fun Board.won(stone: Stone, pos: Pos): Boolean {
        fun won(moves: ListK<Option<Stone>>, stone: Stone): Boolean = moves.foldLeft(listOf(0)) { counts, optMoves ->
            optMoves.filter { it == stone }.fold(
                    { 0 prependTo counts },
                    { (counts.first() + 1) prependTo counts.tail() }
            )
        }.max() ?: 0 >= winCount

        return won(horMoves(pos.x, pos.y, winCount), stone) || won(vertMoves(pos.x, pos.y, winCount), stone) ||
                won(diagRightMoves(pos.x, pos.y, winCount), stone) || won(diagLeftMoves(pos.x, pos.y, winCount), stone)
    }

    /** Get window of horizontal moves at (x, y). */
    fun Board.horMoves(x: Int, y: Int, window: Int): ListK<Option<Stone>> = ListK.monad().binding {
        val xRange = (kotlin.math.max(0, x - (window - 1)) until x + window).toList().k()
        val xx = xRange.bind()
        get(Pos(xx, y))
    }.fix()

    /** Get window of vertical moves at (x, y). */
    fun Board.vertMoves(x: Int, y: Int, window: Int): ListK<Option<Stone>> = ListK.monad().binding {
        val yRange = (kotlin.math.max(0, y - (window - 1)) until y + window).toList()
        val yy = yRange.k().bind()
        get(Pos(x, yy))
    }.fix()

    /** Get window of moves diagonal from top to right bottom at (x, y). */
    fun Board.diagRightMoves(x: Int, y: Int, window: Int): ListK<Option<Stone>> = ListK.monad().binding {
        val xRange = (kotlin.math.max(0, x - (window - 1)) until x + window).toList()
        val yRange = (kotlin.math.max(0, y - (window - 1)) until y + window).toList()
        val (xx, yy) = xRange.zip(yRange).k().bind()
        get(Pos(xx, yy))
    }.fix()

    /** Get window of moves diagonal from bottom to left top at (x, y). */
    fun Board.diagLeftMoves(x: Int, y: Int, window: Int): ListK<Option<Stone>> = ListK.monad().binding {
        val xRange = (kotlin.math.max(0, x - (window - 1)) until x + window).toList()
        val yRange = (kotlin.math.max(0, y - (window - 1)) until y + window).reversed().toList()
        val (xx, yy) = xRange.zip(yRange).k().bind()
        get(Pos(xx, yy))
    }.fix()

    operator fun <F, S, A> StateT.Companion.invoke(MF: Monad<F>, run: StateTFun<F, S, A>, dummy: Unit = Unit): StateT<F, S, A> = StateT(MF.pure(run))

}
