package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignupBinding
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard
import ru.netology.nmedia.validation.ValidationUser.Companion.validName
import ru.netology.nmedia.validation.ValidationUser.Companion.validPassword
import ru.netology.nmedia.validation.ValidationUser.Companion.validUser
import ru.netology.nmedia.viewmodel.SignUpViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignupBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            userEditText.requestFocus()
            signupButton.setOnClickListener {
                val checkName = validName(userEditText.text.toString())
                val checkPassword = validPassword(password.text.toString())
                val checkUser = validUser(username.text.toString())


                if (checkPassword == null && checkUser == null && checkName == null) {
                    viewModel.registrationUser(
                        userEditText.text.toString(),
                        username.text.toString(),
                        password.text.toString(),
                    )
                } else {
                    userContainer.helperText = checkUser ?: "User is good"
                    loginPasswordContainer.helperText = checkPassword ?: "Pass is good"
                    loginContainer.helperText = checkUser ?: "Login is good"
                }
                hideKeyboard(requireView())
            }
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigate(R.id.action_signUpFragment_to_containerFragmentView)
        }

        return binding.root

    }



}