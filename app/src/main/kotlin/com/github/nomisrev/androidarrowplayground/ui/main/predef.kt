package com.github.nomisrev.androidarrowplayground.ui.main

import arrow.core.ForId
import arrow.dagger.effects.instances.ArrowEffectsInstances
import arrow.dagger.instances.ArrowInstances
import com.github.nomisrev.androidarrowplayground.ActivityModule
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import dagger.Component

@Component(modules = [
    ActivityModule::class,
    ArrowInstances::class,
    ArrowEffectsInstances::class
])
interface MainInstances {
    fun navigator(): Navigator<ForId>
}

fun MainActivity.instances(): MainInstances = DaggerMainInstances
        .builder()
        .activityModule(ActivityModule(this))
        .build()
