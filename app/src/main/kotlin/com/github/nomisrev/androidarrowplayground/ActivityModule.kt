package com.github.nomisrev.androidarrowplayground

import android.app.Activity
import arrow.core.ForId
import arrow.core.Id
import arrow.core.applicative
import arrow.dagger.effects.instances.ArrowEffectsInstances
import arrow.dagger.instances.ArrowInstances
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import com.github.nomisrev.androidarrowplayground.ui.main.MainActivity
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun activity() = activity

    @Provides //TODO this doesn't work nor can Dagger 2 pickup AndroidNavigation as Navigator as a current workaround I'll fix `F` for now.
//    fun <F> navigator(applicative: Applicative<F>): Navigator<F> = AndroidNavigation(activity, applicative)
    fun navigator(): Navigator<ForId> = AndroidNavigation(activity, Id.applicative())

}
