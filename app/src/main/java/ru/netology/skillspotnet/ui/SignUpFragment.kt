package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.FragmentSignupBinding
import ru.netology.skillspotnet.util.AndroidUtils.hideKeyboard
import ru.netology.skillspotnet.validation.ValidationUser.Companion.validName
import ru.netology.skillspotnet.validation.ValidationUser.Companion.validPassword
import ru.netology.skillspotnet.validation.ValidationUser.Companion.validUser
import ru.netology.skillspotnet.viewmodel.SignUpViewModel
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