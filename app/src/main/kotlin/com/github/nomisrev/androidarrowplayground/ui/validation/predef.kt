package com.github.nomisrev.androidarrowplayground.ui.validation

import arrow.core.ForId
import arrow.dagger.effects.instances.ArrowEffectsInstances
import arrow.dagger.instances.ArrowInstances
import com.github.nomisrev.androidarrowplayground.ActivityModule
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import com.github.nomisrev.androidarrowplayground.domain.validation.ValidationPresenter
import dagger.Component

@Component(modules = [
    ActivityModule::class,
    ArrowInstances::class,
    ArrowEffectsInstances::class
])
interface ValidationInstances {
    fun navigator(): Navigator<ForId>
    fun presenter(): ValidationPresenter<ForId>
}

fun ValidationActivity.instances(): ValidationInstances = DaggerValidationInstances
        .builder()
        .activityModule(ActivityModule(this))
        .build()
