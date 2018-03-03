package com.github.nomisrev.androidarrowplayground.ui.validation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import arrow.data.NonEmptyList
import com.github.nomisrev.androidarrowplayground.*
import com.github.nomisrev.androidarrowplayground.domain.validation.*
import kotlinx.android.synthetic.main.activity_validation.*

/**
 * Validation form implemented in a MVP-ish style. Where Activity implements [ValidationView] interface.
 */
class ValidationActivity : AppCompatActivity(), ValidationView {

    private val instances = instances()

    /**
     * On click or submit we validate the form and run the effect.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validation)

        emailInput.onImeActionDone { _ ->
            instances.presenter().validate(this, getFormData())
        }

        confirm.onClick {
            instances.presenter().validate(this, getFormData())
        }

    }

    private fun getFormData() = User(
            userInput.text.toString(),
            passwordInput.text.toString(),
            nameInput.text.toString(),
            lastNameInput.text.toString(),
            emailInput.text.toString()
    )

    override fun showCorrectFormResult(user: User) {
        Snackbar.make(container, R.string.saved_success, Snackbar.LENGTH_LONG).show()
    }

    override fun showIncorrectForm(errors: NonEmptyList<DomainValidation>) {
        errors.foldLeft(Unit) { _, error ->
            when (error) {
                UsernameHasSpecialCharacters -> user.error = getString(R.string.username_error)
                PasswordDoesNotMeetCriteria -> password.error = getString(R.string.password_error)
                FirstNameHasSpecialCharacters -> name.error = getString(R.string.name_error)
                LastNameHasSpecialCharacters -> lastName.error = getString(R.string.lastname_error)
                EmailIsInvalid -> email.error = getString(R.string.email_error)
            }
        }
    }

}
