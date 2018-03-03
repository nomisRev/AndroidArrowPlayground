package com.github.nomisrev.androidarrowplayground.ui.tictactoe

import arrow.core.*
import arrow.dagger.effects.instances.ArrowEffectsInstances
import arrow.dagger.instances.ArrowInstances
import arrow.data.StateTPartialOf
import com.github.nomisrev.androidarrowplayground.ActivityModule
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import com.github.nomisrev.androidarrowplayground.domain.tictactoe.*
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class TicTacToeModule {

    @Provides
    fun tictactoe(): TicTacToe<StateTPartialOf<EitherPartialOf<TicTacToeError>, TicTacToeState>> = TicTacToeGame()

}

@Component(modules = [
    ActivityModule::class,
    ArrowInstances::class,
    TicTacToeModule::class,
    ArrowEffectsInstances::class
])
interface TicTacToeInstances {
    fun navigator(): Navigator<ForId>
    fun game(): RxTicTacToePresenter
}

fun TicTacToeActivity.instances(): TicTacToeInstances = DaggerTicTacToeInstances
        .builder()
        .activityModule(ActivityModule(this))
        .build()