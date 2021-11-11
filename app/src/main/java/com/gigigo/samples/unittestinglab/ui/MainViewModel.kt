package com.gigigo.samples.unittestinglab.ui

import androidx.lifecycle.viewModelScope
import com.gigigo.samples.unittestinglab.domain.SignUpData
import com.gigigo.samples.unittestinglab.domain.SignUpUseCase
import com.gigigo.samples.unittestinglab.presentation.BaseViewModel
import com.gigigo.samples.unittestinglab.presentation.CoroutinesDispatchers
import com.gigigo.samples.unittestinglab.presentation.EmailValidator
import com.gigigo.samples.unittestinglab.presentation.MinLengthTextValidator
import com.gigigo.samples.unittestinglab.presentation.NotEmptyTextValidator
import kotlinx.coroutines.launch

sealed class MainViewIntent {

    class EmailChanged(val value: String) : MainViewIntent()
    class NameChanged(val value: String) : MainViewIntent()
    class PasswordChanged(val value: String) : MainViewIntent()
    class PasswordConfirmationChanged(val value: String) : MainViewIntent()
    object SignUp : MainViewIntent()
}

data class MainViewState(
    val isLoading: Boolean = false,
    val email: Field = Field(),
    val name: Field = Field(),
    val password: Field = Field(),
    val passwordConfirmation: Field = Field(),
    val isSignUpEnabled: Boolean = false,
    val signUpResult: Boolean? = null
)

data class Field(
    val value: String? = null,
    val error: String? = null
)

class MainViewModel(
    private val signUp: SignUpUseCase,
    dispatchers: CoroutinesDispatchers
) : BaseViewModel<MainViewState, MainViewIntent>(dispatchers) {

    override val initialViewState: MainViewState = MainViewState()

    private val emailValidator = EmailValidator()

    private val passwordValidator = MinLengthTextValidator(6)

    private val nameValidator = NotEmptyTextValidator()

    private var currentMail: String? = null
    private var currentName: String? = null
    private var currentPassword: String? = null
    private var currentPasswordConfirmation: String? = null

    override fun sendIntent(intent: MainViewIntent) {
        when (intent) {
            is MainViewIntent.EmailChanged -> onEmailChanged(intent.value)
            is MainViewIntent.NameChanged -> onNameChanged(intent.value)
            is MainViewIntent.PasswordChanged -> onPasswordChanged(intent.value)
            is MainViewIntent.PasswordConfirmationChanged -> onPasswordConfirmationChanged(intent.value)
            is MainViewIntent.SignUp -> onSignUp()
        }
    }

    private fun onEmailChanged(value: String) {
        currentMail = value
        setState { copy(email = email.copy(value = value)) }
        checkFields()
    }

    private fun onNameChanged(value: String) {
        currentName = value
        setState { copy(name = name.copy(value = value)) }
        checkFields()
    }

    private fun onPasswordChanged(value: String) {
        currentPassword = value
        setState { copy(password = password.copy(value = value)) }
        checkFields()
    }

    private fun onPasswordConfirmationChanged(value: String) {
        currentPasswordConfirmation = value
        setState { copy(passwordConfirmation = passwordConfirmation.copy(value = value)) }
        checkFields()
    }

    private fun onSignUp() = viewModelScope.launch {
        setState { copy(isLoading = true, isSignUpEnabled = false) }
        val data = SignUpData(
            email = requireNotNull(currentMail),
            name = requireNotNull(currentName),
            password = requireNotNull(currentPassword),
        )
        val success = signUp(data)
        setState { copy(isLoading = false, isSignUpEnabled = true, signUpResult = success) }
    }

    private fun checkFields() {
        val isEmailValid = emailValidator.validate(currentMail.orEmpty())
        val isNameValid = nameValidator.validate(currentName.orEmpty())
        val isPasswordValid = passwordValidator.validate(currentPassword.orEmpty())
        val isPasswordConfirmationValid = currentPassword == currentPasswordConfirmation
        val isSignUpEnabled =
            isEmailValid && isNameValid && isPasswordValid && isPasswordConfirmationValid

        setState {
            copy(
                email = email.copy(
                    error = "Invalid mail".takeIf { !isEmailValid && currentMail != null }
                ),
                name = name.copy(
                    error = "Mandatory".takeIf { !isNameValid && currentName != null }
                ),
                password = password.copy(
                    error = "Too short".takeIf { !isPasswordValid && currentPassword != null }
                ),
                passwordConfirmation = passwordConfirmation.copy(
                    error = "Passwords don't match".takeIf {
                        !isPasswordConfirmationValid && currentPasswordConfirmation != null
                    }
                ),
                isSignUpEnabled = isSignUpEnabled
            )
        }
    }

}
