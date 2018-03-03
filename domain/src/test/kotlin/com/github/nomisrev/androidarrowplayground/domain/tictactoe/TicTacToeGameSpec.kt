package com.github.nomisrev.androidarrowplayground.domain.tictactoe

import arrow.core.*
import arrow.data.*
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.foldable.fold
import arrow.syntax.option.none
import arrow.syntax.option.some
import arrow.typeclasses.Eq
import arrow.typeclasses.binding
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TicTacToeGameSpec : StringSpec() {

    init {
        fun Board.toState() = TicTacToeState(this, Stone.X)

        val game = TicTacToeGame()
        val eq: Eq<Either<TicTacToeError, Option<Stone>>> = Eq.any()

        "Resetting the board" {
            val horizontalWinnerX = listOf(
                    listOf(none<Stone>(), none(), none(), none(), none(), none()).k(),
                    listOf(none<Stone>(), none(), none(), none(), none()).k(),
                    listOf(Stone.X.some(), none(), none(), none(), none()).k(),
                    listOf(Stone.X.some(), none(), none(), none(), none()).k(),
                    listOf(Stone.X.some(), none(), none(), none(), none()).k()
            ).k().toState()

            game.reset().runS(horizontalWinnerX, Either.monad()) shouldBe TicTacToeGame.empty.right()
        }

        "Placing something on the board" {
            game.place(Pos(0, 0)).runS(TicTacToeGame.empty, Either.monad()).fix()
                    .map { it.board[Pos(0, 0)] } shouldBe Stone.X.some().right()
        }

        "Placing outside of board" {
            game.place(Pos(10, 10)).runM(TicTacToeGame.empty).fix() shouldBe NotInTheBoard(Pos(10, 10)).left()
        }

        "For a winning horizontal row winner should be found" {
            forAll(Gen.oneOf(listOf(Stone.X, Stone.O))) { stone ->
                val horizontalWinnerX = listOf(
                        listOf(none<Stone>(), none(), none(), none(), none(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(stone.some(), none(), none(), none(), none()).k(),
                        listOf(stone.some(), none(), none(), none(), none()).k(),
                        listOf(stone.some(), none(), none(), none(), none()).k()
                ).k().toState()

                eq.eqv(
                        game.winner().fix().runA(horizontalWinnerX, Either.monad()).fix(),
                        stone.some().right()
                )
            }
        }

        "For a winning vertical row winner should be found" {
            forAll(Gen.oneOf(listOf(Stone.X, Stone.O))) { stone ->
                val verticalWinner = listOf(
                        listOf(none(), none(), stone.some(), stone.some(), stone.some()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k()
                ).k().toState()

                eq.eqv(
                        game.winner().fix().runA(verticalWinner, Either.monad()).fix(),
                        stone.some().right()
                )
            }
        }

        "For a winning dialog right row winner should be found" {
            forAll(Gen.oneOf(listOf(Stone.X, Stone.O))) { stone ->
                val verticalWinner = listOf(
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(none(), stone.some(), none(), none(), none()).k(),
                        listOf(none(), none(), stone.some(), none(), none()).k(),
                        listOf(none(), none(), none(), stone.some(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k()
                ).k().toState()

                eq.eqv(
                        game.winner().fix().runA(verticalWinner, Either.monad()).fix(),
                        stone.some().right()
                )
            }
        }

        "For a winning dialog left row winner should be found" {
            forAll(Gen.oneOf(listOf(Stone.X, Stone.O))) { stone ->
                val verticalWinner = listOf(
                        listOf(none<Stone>(), none(), none(), none(), none()).k(),
                        listOf(none(), none(), none(), stone.some(), none()).k(),
                        listOf(none(), none(), stone.some(), none(), none()).k(),
                        listOf(none(), stone.some(), none(), none(), none()).k(),
                        listOf(none<Stone>(), none(), none(), none(), none()).k()
                ).k().toState()

                eq.eqv(
                        game.winner().fix().runA(verticalWinner, Either.monad()).fix(),
                        stone.some().right()
                )
            }
        }

        "Checking stone on pos" {
            StateT.monad<EitherPartialOf<TicTacToeError>, TicTacToeState>(Either.monad()).binding {
                game.on(Pos(0, 0)).bind() shouldBe None
                game.place(Pos(0, 0)).bind()
                game.on(Pos(0, 0)).bind() shouldBe Some(Stone.X)
            }.runM(TicTacToeGame.empty)
        }

        "Check current turn" {
            forAll(Gen.oneOf(listOf(Stone.O, Stone.X))) { stone ->
                game.turn().runA(TicTacToeGame.empty.copy(turn = stone), Either.monad()).fix() == stone.some().right()
            }
        }

    }

}