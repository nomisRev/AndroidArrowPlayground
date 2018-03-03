package com.github.nomisrev.androidarrowplayground.domain.validation

import arrow.data.Validated
import arrow.data.ValidatedNel
import arrow.syntax.validated.invalidNel
import arrow.syntax.validated.valid

data class User(
        val username: String,
        val password: String,
        val name: String,
        val lastName: String,
        val email: String
)

typealias ValidationForm = User

sealed class DomainValidation
object UsernameHasSpecialCharacters : DomainValidation()
object PasswordDoesNotMeetCriteria : DomainValidation()
object FirstNameHasSpecialCharacters : DomainValidation()
object LastNameHasSpecialCharacters : DomainValidation()
object EmailIsInvalid : DomainValidation()

/**
 * Utility functions to remove some boilerplate for evaluating a form.
 */
typealias ValidationResult<A> = ValidatedNel<DomainValidation, A>

fun <E> Validated.Companion.cond(regex: Regex, a: String, e: E): ValidatedNel<E, String> =
        if (a.matches(regex)) a.valid() else e.invalidNel()

fun <E> Validated.Companion.cond(f: (String) -> Boolean, a: String, e: E): ValidatedNel<E, String> =
        if (f(a)) a.valid() else e.invalidNel()

fun <E> Validated.Companion.cond(boolean: Boolean, a: String, e: E): ValidatedNel<E, String> =
        if (boolean) a.valid() else e.invalidNel()