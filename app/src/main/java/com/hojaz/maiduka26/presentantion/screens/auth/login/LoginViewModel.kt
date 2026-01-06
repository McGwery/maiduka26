package com.hojaz.maiduka26.presentantion.screens.auth.login

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.usecase.auth.LoginUseCase
import com.hojaz.maiduka26.domain.usecase.auth.LoginWithPhoneUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase
) : BaseViewModel<LoginState, LoginEvent, LoginEffect>() {

    override fun createInitialState(): LoginState = LoginState()

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                setState { copy(email = event.email, emailError = null, error = null) }
            }
            is LoginEvent.PhoneChanged -> {
                setState { copy(phone = event.phone, phoneError = null, error = null) }
            }
            is LoginEvent.PasswordChanged -> {
                setState { copy(password = event.password, passwordError = null, error = null) }
            }
            is LoginEvent.ToggleLoginMethod -> {
                setState { copy(isEmailLogin = !isEmailLogin, error = null) }
            }
            is LoginEvent.Login -> {
                performLogin()
            }
            is LoginEvent.ClearError -> {
                setState { copy(error = null) }
            }
            is LoginEvent.NavigateToRegister -> {
                setEffect(LoginEffect.NavigateToRegister)
            }
            is LoginEvent.NavigateToForgotPassword -> {
                setEffect(LoginEffect.NavigateToForgotPassword)
            }
        }
    }

    private fun performLogin() {
        if (!validateInput()) return

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val result = if (currentState.isEmailLogin) {
                loginUseCase(currentState.email.trim(), currentState.password)
            } else {
                loginWithPhoneUseCase(currentState.phone.trim(), currentState.password)
            }

            result.fold(
                ifLeft = { error ->
                    setState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Login failed"
                        )
                    }
                    setEffect(LoginEffect.ShowSnackbar(error.message ?: "Login failed"))
                },
                ifRight = { user ->
                    setState { copy(isLoading = false, isLoginSuccessful = true) }
                    setEffect(LoginEffect.NavigateToHome)
                }
            )
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (currentState.isEmailLogin) {
            if (currentState.email.isBlank()) {
                setState { copy(emailError = "Email is required") }
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
                setState { copy(emailError = "Invalid email format") }
                isValid = false
            }
        } else {
            if (currentState.phone.isBlank()) {
                setState { copy(phoneError = "Phone number is required") }
                isValid = false
            } else if (currentState.phone.length < 10) {
                setState { copy(phoneError = "Invalid phone number") }
                isValid = false
            }
        }

        if (currentState.password.isBlank()) {
            setState { copy(passwordError = "Password is required") }
            isValid = false
        } else if (currentState.password.length < 6) {
            setState { copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        return isValid
    }
}

