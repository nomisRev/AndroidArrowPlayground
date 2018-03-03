package com.github.nomisrev.androidarrowplayground.domain

import arrow.Kind
import arrow.typeclasses.Applicative

interface Navigator<F> {

    val applicative: Applicative<F>

    fun goToValidation(): Kind<F, Unit>

    fun goToTicTacToe(): Kind<F, Unit>

    fun goBack(): Kind<F, Unit>

}
