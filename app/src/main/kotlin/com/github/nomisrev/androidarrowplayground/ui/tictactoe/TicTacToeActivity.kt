package com.github.nomisrev.androidarrowplayground.ui.tictactoe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import arrow.core.*
import arrow.data.ListK
import arrow.data.k
import com.github.nomisrev.androidarrowplayground.R
import com.github.nomisrev.androidarrowplayground.domain.tictactoe.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_tictactoe.*

class TicTacToeActivity : AppCompatActivity() {

    val instances = instances()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tictactoe)

        val boardEvents = Observable.merge(listOf(
                x0y0.clicks().map { Pos(0, 0) },
                x0y1.clicks().map { Pos(0, 1) },
                x0y2.clicks().map { Pos(0, 2) },
                x1y0.clicks().map { Pos(1, 0) },
                x1y1.clicks().map { Pos(1, 1) },
                x1y2.clicks().map { Pos(1, 2) },
                x2y0.clicks().map { Pos(2, 0) },
                x2y1.clicks().map { Pos(2, 1) },
                x2y2.clicks().map { Pos(2, 2) }
        ))

        instances.game().play(boardEvents).subscribe(::render)
    }

    /** View only contains some simple render logic. */
    private fun render(viewModel: TicTacToeViewModel) {
        when (viewModel) {
            is OverWithError -> renderError(viewModel)
            is OverWithWinner -> renderWon(viewModel)
            is InProgress -> renderInProgress(viewModel)
        }
    }

    private fun renderInProgress(vm: InProgress) {
        game_state.text = "Player ${vm.state.turn.opponent.symbol}'s turn"
        renderBoard(vm.state.board)
    }

    private fun renderWon(vm: OverWithWinner) {
        game_state.text = "Player ${vm.winner.symbol} won!"
        renderBoard(vm.state.board)
        renderGameOver()
    }

    private fun renderError(vm: OverWithError) {
        when (vm.error) {
            is OccupiedPosition -> game_state.text = "Position ${(vm.error as OccupiedPosition).position} is already taken"
            is NotInTheBoard -> game_state.text = "Position ${(vm.error as NotInTheBoard).position} is not on board" //Cannot occur because UI doesn't allow this.
        }
        renderGameOver()
    }

    private fun firstColumn() = listOf(x0y0, x0y1, x0y2)
    private fun secondColumn() = listOf(x1y0, x1y1, x1y2)
    private fun thirdColumn() = listOf(x2y0, x2y1, x2y2)

    private fun renderBoard(board: Board) = board.mapIndexed { index: Int, listK: ListK<Option<Stone>> ->
        when (index) {
            0 -> firstColumn() zip listK
            1 -> secondColumn() zip listK
            2 -> thirdColumn() zip listK
            else -> emptyList()
        }
    }.k().flatten().forEach { (button, optStone) ->
        button.text = optStone.map { it.symbol }.getOrElse { "" }
        button.isEnabled = optStone.isEmpty()
    }

    private fun renderGameOver() = (firstColumn() + secondColumn() + thirdColumn()).forEach {
        it.isEnabled = false
    }

}