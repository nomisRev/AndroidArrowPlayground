private const val kotlinVersion = "1.2.30"
private const val androidGradleVersion = "3.0.1"

private const val arrowVersion = "0.6.2-SNAPSHOT"

private const val supportVersion = "27.0.2"
private const val constraintVersion = "1.1.0-beta5"

private const val androidCoroutinesVersion = "0.22.3"

private const val daggerVersion = "2.5"

private const val retrofitVersion = "2.3.0"
private const val okhttpVersion = "3.9.1"

private const val conductorVersion = "2.1.4"

private const val kotlinTestVersion = "2.0.7"

private const val rxRelayVersion = "2.0.0"
private const val rxAndroidVersion = "2.0.2"
private const val rxBindingVersion = "2.1.1"

object BuildPlugins {
    val android = "com.android.tools.build:gradle:$androidGradleVersion"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
}

object AndroidConfig {
    val buildToolsVersion = "27.0.3"
    val minSdkVersion = 21
    val targetSdkVersion = 27
    val compileSdkVersion = 27
    val baseApplicationId = "com.github.nomisRev"
    val versionCode = 1
    val versionName = "0.1"
}

object Libs {
    val kotlin_std = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"

    val arrow = arrayOf(
            "io.arrow-kt:arrow-core:$arrowVersion",
            "io.arrow-kt:arrow-typeclasses:$arrowVersion",
            "io.arrow-kt:arrow-data:$arrowVersion",
            "io.arrow-kt:arrow-instances:$arrowVersion",
            "io.arrow-kt:arrow-syntax:$arrowVersion",
            "io.arrow-kt:arrow-free:$arrowVersion",
            "io.arrow-kt:arrow-mtl:$arrowVersion",
            "io.arrow-kt:arrow-effects:$arrowVersion",
            "io.arrow-kt:arrow-effects-rx2:$arrowVersion",
            "io.arrow-kt:arrow-effects-kotlinx-coroutines:$arrowVersion",
            "io.arrow-kt:arrow-optics:$arrowVersion",
            "io.arrow-kt:arrow-dagger-effects:$arrowVersion",
            "io.arrow-kt:arrow-dagger-effects-rx2:$arrowVersion",
            "io.arrow-kt:arrow-dagger-effects-kotlinx-coroutines:$arrowVersion"
    )

    val arrowCompiler = arrayOf(
            "io.arrow-kt:arrow-annotations-processor:$arrowVersion",
            "com.google.dagger:dagger-compiler:$daggerVersion"
    )

    val android = arrayOf(
            "com.android.support:appcompat-v7:$supportVersion",
            "com.android.support:recyclerview-v7:$supportVersion",
            "com.android.support:cardview-v7:$supportVersion",
            "com.android.support:palette-v7:$supportVersion",
            "com.android.support:design:$supportVersion",
            "com.android.support.constraint:constraint-layout:$constraintVersion",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$androidCoroutinesVersion"
    )

    val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
    val okhttp = "com.squareup.okhttp3:okhttp:$okhttpVersion"
    val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:$okhttpVersion"

    val daggerCompiler = "com.google.dagger:dagger-compiler:$daggerVersion"
    val dagger = "com.google.dagger:dagger:$daggerVersion"

    val conductor = "com.bluelinelabs:conductor:$conductorVersion"

    val rxRelay = "com.jakewharton.rxrelay2:rxrelay:$rxRelayVersion"

    val rxAndroid = arrayOf(
            "io.reactivex.rxjava2:rxandroid:$rxAndroidVersion",
            "com.jakewharton.rxbinding2:rxbinding-kotlin:$rxBindingVersion"
    )

}

object TestLibs {
    val kotlinTest = "io.kotlintest:kotlintest:$kotlinTestVersion"
}
