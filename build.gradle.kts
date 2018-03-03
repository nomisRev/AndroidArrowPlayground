import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories

buildscript {

    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath(BuildPlugins.android)
        classpath(BuildPlugins.kotlin)
    }

}

allprojects {
    repositories {
        jcenter()
        google()
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
    }
}