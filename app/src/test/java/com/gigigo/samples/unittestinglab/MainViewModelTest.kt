package com.gigigo.samples.unittestinglab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gigigo.samples.unittestinglab.domain.SignUpData
import com.gigigo.samples.unittestinglab.domain.SignUpUseCase
import com.gigigo.samples.unittestinglab.presentation.CoroutinesDispatchers
import com.gigigo.samples.unittestinglab.ui.MainViewIntent
import com.gigigo.samples.unittestinglab.ui.MainViewModel
import com.gigigo.samples.unittestinglab.ui.MainViewState
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private val dispatchers: CoroutinesDispatchers = TestCoroutinesDispatchers(
        coroutinesTestRule.testDispatcher
    )

    @Test
    fun `Given idle state Then ViewState should be idle`() {

        val viewModel = MainViewModel(
            signUp = buildSignUpUseCaseWithResult(true),
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        observer.assertThat {
            allOf(
                !isLoading,
                email.value == null,
                email.error == null,
                name.value == null,
                name.error == null,
                password.value == null,
                password.error == null,
                passwordConfirmation.value == null,
                passwordConfirmation.error == null,
                !isSignUpEnabled,
                signUpResult == null
            )
        }
    }

    @Test
    fun `When all fields valid Then sign up is enabled`() {

        //GIVEN
        val viewModel = MainViewModel(
            signUp = buildSignUpUseCaseWithResult(true),
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        //WHEN
        viewModel.sendIntent(MainViewIntent.EmailChanged("domain@example.com"))
        viewModel.sendIntent(MainViewIntent.NameChanged("name"))
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password"))

        //THEN
        observer.assertThat { isSignUpEnabled }
    }

    @Test
    fun `When passwords don't match Then sign up is disabled`() {

        //GIVEN
        val viewModel = MainViewModel(
            signUp = buildSignUpUseCaseWithResult(true),
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        //WHEN
        viewModel.sendIntent(MainViewIntent.EmailChanged("domain@example.com"))
        viewModel.sendIntent(MainViewIntent.NameChanged("name"))
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password1"))

        //THEN
        observer.assertThat { !isSignUpEnabled }
    }

    @Test
    fun `When passwords don't match Then confirmation error is shown`() {

        //GIVEN
        val viewModel = MainViewModel(
            signUp = buildSignUpUseCaseWithResult(true),
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        //WHEN
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password1"))

        //THEN
        observer.assertThat { passwordConfirmation.error != null }
    }

    @Test
    fun `Given valid data When sign up is success Then success result is shown`() {

        //GIVEN
        val signUpUseCase = buildSignUpUseCaseWithResult(true)
        val viewModel = MainViewModel(
            signUp = signUpUseCase,
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        viewModel.sendIntent(MainViewIntent.EmailChanged("domain@example.com"))
        viewModel.sendIntent(MainViewIntent.NameChanged("name"))
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password"))

        //WHEN
        viewModel.sendIntent(MainViewIntent.SignUp)

        //THEN
        observer.assertThat { signUpResult == true }
    }

    @Test
    fun `Given valid data When sign up fails Then error result is shown`() {

        //GIVEN
        val signUpUseCase = buildSignUpUseCaseWithResult(false)
        val viewModel = MainViewModel(
            signUp = signUpUseCase,
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        viewModel.sendIntent(MainViewIntent.EmailChanged("domain@example.com"))
        viewModel.sendIntent(MainViewIntent.NameChanged("name"))
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password"))

        //WHEN
        viewModel.sendIntent(MainViewIntent.SignUp)

        //THEN
        observer.assertThat { signUpResult == false }
    }

    @Test
    fun `Given valid data When sign up is clicked Then sign up process is called`() {

        //GIVEN
        val signUpUseCase = mock<SignUpUseCase> { onBlocking { invoke(any()) } doReturn true }
        val viewModel = MainViewModel(
            signUp = signUpUseCase,
            dispatchers = dispatchers
        )
        val observer = TestObserver<MainViewState>()
        viewModel.viewState.observeForever(observer)

        viewModel.sendIntent(MainViewIntent.EmailChanged("domain@example.com"))
        viewModel.sendIntent(MainViewIntent.NameChanged("name"))
        viewModel.sendIntent(MainViewIntent.PasswordChanged("password"))
        viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged("password"))

        //WHEN
        viewModel.sendIntent(MainViewIntent.SignUp)

        //THEN
        verifyBlocking(signUpUseCase) { invoke(any()) }
    }

    private fun buildSignUpUseCaseWithResult(result: Boolean) = object : SignUpUseCase {
        override suspend fun invoke(data: SignUpData): Boolean = result
    }

    private fun <T> TestObserver<T>.assertThat(block: T.() -> Boolean) {
        Assert.assertTrue(values.last().block())
    }

    private fun allOf(vararg conditions: Boolean) = conditions.all { it }
}
