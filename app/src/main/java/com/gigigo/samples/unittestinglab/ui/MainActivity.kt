package com.gigigo.samples.unittestinglab.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.gigigo.samples.unittestinglab.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            tilEmail.onTextChanged {
                viewModel.sendIntent(MainViewIntent.EmailChanged(it))
            }
            tilName.onTextChanged {
                viewModel.sendIntent(MainViewIntent.NameChanged(it))
            }
            tilPassword.onTextChanged {
                viewModel.sendIntent(MainViewIntent.PasswordChanged(it))
            }
            tilPasswordConfirmation.onTextChanged {
                viewModel.sendIntent(MainViewIntent.PasswordConfirmationChanged(it))
            }
            btSignUp.setOnClickListener {
                viewModel.sendIntent(MainViewIntent.SignUp)
            }
        }

        viewModel.viewState.observe(this) { state ->
            with(binding) {
                tilEmail.setTextIfDiffers(state.email.value)
                tilName.setTextIfDiffers(state.name.value)
                tilPassword.setTextIfDiffers(state.password.value)
                tilPasswordConfirmation.setTextIfDiffers(state.passwordConfirmation.value)
                tilEmail.error = state.email.error
                tilName.error = state.name.error
                tilPassword.error = state.password.error
                tilPasswordConfirmation.error = state.passwordConfirmation.error
                btSignUp.isEnabled = state.isSignUpEnabled
                loadingView.isVisible = state.isLoading
            }
        }
    }

    private fun TextInputLayout.onTextChanged(block: (String) -> Unit) {
        editText?.doAfterTextChanged { block(it?.toString().orEmpty()) }
    }

    private fun TextInputLayout.setTextIfDiffers(text: String?) {
        if (editText?.text?.toString().orEmpty() == text.orEmpty()) return

        editText?.setText(text, TextView.BufferType.EDITABLE)
    }

}
