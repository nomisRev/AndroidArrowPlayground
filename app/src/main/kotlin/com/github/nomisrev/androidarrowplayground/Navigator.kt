package com.github.nomisrev.androidarrowplayground

import android.app.Activity
import android.content.Intent
import arrow.Kind
import arrow.typeclasses.Applicative
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import com.github.nomisrev.androidarrowplayground.ui.tictactoe.TicTacToeActivity
import com.github.nomisrev.androidarrowplayground.ui.validation.ValidationActivity
import javax.inject.Inject

class AndroidNavigation<F> @Inject constructor(
        private val activity: Activity,
        override val applicative: Applicative<F>
) : Navigator<F> {

    override fun goToValidation(): Kind<F, Unit> = applicative.pure(activity.runOnUiThread {
        activity.startActivity(Intent(activity, ValidationActivity::class.java))
    })

    override fun goToTicTacToe(): Kind<F, Unit> = applicative.pure(activity.runOnUiThread {
        activity.startActivity(Intent(activity, TicTacToeActivity::class.java))
    })

    override fun goBack(): Kind<F, Unit> = applicative.pure(activity.runOnUiThread {
        activity.onBackPressed()
    })

}