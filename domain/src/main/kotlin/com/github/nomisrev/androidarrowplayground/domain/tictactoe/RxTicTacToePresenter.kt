package com.github.nomisrev.androidarrowplayground.domain.tictactoe

import arrow.core.*
import arrow.data.StateTPartialOf
import arrow.data.fix
import io.reactivex.Observable
import javax.inject.Inject

/** Aliases to work with sealed classes. */
typealias OverWithError = TicTacToeViewModel.OverWithError
typealias OverWithWinner = TicTacToeViewModel.OverWithWinner
typealias InProgress = TicTacToeViewModel.InProgress

/**
 * Sealed class that represents the model to display in the UI.
 */
sealed class TicTacToeViewModel {
    data class InProgress(val state: TicTacToeState) : TicTacToeViewModel()
    data class OverWithWinner(val winner: Stone, val state: TicTacToeState) : TicTacToeViewModel()
    data class OverWithError(val error: TicTacToeError) : TicTacToeViewModel()
}

/**
 * [RxTicTacToePresenter] contains the logic to play a using Rx.
 *
 * You can play a game by calling [play].
 */
class RxTicTacToePresenter @Inject constructor(val game: TicTacToe<StateTPartialOf<EitherPartialOf<TicTacToeError>, TicTacToeState>>) {

    /**
     * Maps a stream of [Pos] to a stream of [TicTacToeViewModel] using an instance of [TicTacToe].
     *
     * @boardClicks a stream of [Pos] to play on the tic tac toe board.
     * @return a stream of [TicTacToeViewModel] which can either represent the in progress state, over state with winner or error.
     */
    fun play(boardClicks: Observable<Pos>): Observable<TicTacToeViewModel> =
            boardClicks.scan(InProgress(TicTacToeGame.empty) as TicTacToeViewModel) { vm: TicTacToeViewModel, pos: Pos ->
                when (vm) {
                    is TicTacToeViewModel.OverWithError -> vm
                    is TicTacToeViewModel.OverWithWinner -> vm
                    is TicTacToeViewModel.InProgress -> playTurn(vm.state, pos)
                }
            }

    /** Play a [Pos] on the board and check if anyone won. */
    private fun playTurn(state: TicTacToeState, pos: Pos): TicTacToeViewModel = game.place(pos).fix().runS(state, Either.monad()).fix().fold(
            ::OverWithError,
            ::checkIfAnyoneWon
    )

    /** Check if anyone won. In case someone won return [OverWithWinner] else return [InProgress]. */
    private fun checkIfAnyoneWon(state: TicTacToeState): TicTacToeViewModel = game.winner().fix().runA(state, Either.monad()).fix().fold(
            ::OverWithError,
            { optWinner: Option<Stone> ->
                optWinner.fold(
                        ifEmpty = { InProgress(state) },
                        some = { OverWithWinner(it, state) }
                )
            }
    )

}
