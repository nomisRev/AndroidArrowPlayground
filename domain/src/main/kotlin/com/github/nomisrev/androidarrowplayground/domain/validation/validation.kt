package com.github.nomisrev.androidarrowplayground.domain.validation

import arrow.Kind
import arrow.data.*
import arrow.syntax.applicative.map
import arrow.typeclasses.Monad
import arrow.typeclasses.binding
import com.github.nomisrev.androidarrowplayground.domain.Navigator
import javax.inject.Inject

/** View that the UI has to implement in order to show Correct and Incorrect form state. */
interface ValidationView {
    fun showCorrectFormResult(user: User)
    fun showIncorrectForm(errors: NonEmptyList<DomainValidation>)
}

private const val usernameRegex = "^[a-zA-Z0-9]+$"
private const val passwordRegex = "(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"
private const val nameRegex = "^[a-zA-Z]+$"
private const val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""

class ValidationPresenter<F> @Inject constructor(
        private val monad: Monad<F>,
        private val navigation: Navigator<F> // could be used to go back.
) {

    fun validate(view: ValidationView, data: ValidationForm): Kind<F, Unit> = monad.binding {
        validateForm(data).fold(
                view::showIncorrectForm,
                view::showCorrectFormResult
        )
    }

    private fun validateForm(form: ValidationForm): ValidationResult<User> = ValidatedNel.applicative<NonEmptyList<DomainValidation>>().map(
            validateUserName(form.username),
            validatePassword(form.password),
            validateFirstName(form.name),
            validateLastName(form.lastName),
            validateEmail(form.email)
    ) { (username, password, firstName, lastName, age) -> User(username, password, firstName, lastName, age) }.fix()

    private fun validateUserName(userName: String): ValidationResult<String> = Validated.cond(usernameRegex.toRegex(), userName, UsernameHasSpecialCharacters)

    private fun validatePassword(password: String): ValidationResult<String> = Validated.cond(passwordRegex.toRegex(), password, PasswordDoesNotMeetCriteria)

    private fun validateFirstName(firstName: String): ValidationResult<String> = Validated.cond(nameRegex.toRegex(), firstName, FirstNameHasSpecialCharacters)

    private fun validateLastName(lastName: String): ValidationResult<String> = Validated.cond(nameRegex.toRegex(), lastName, LastNameHasSpecialCharacters)

    private fun validateEmail(email: String): ValidationResult<String> = Validated.cond(emailRegex.toRegex(), email, EmailIsInvalid)

}