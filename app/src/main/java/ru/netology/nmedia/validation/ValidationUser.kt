package ru.netology.nmedia.validation

class ValidationUser {
    companion object {
        fun validPassword(passwordText: String): String? {
            if (passwordText.length < 3) {
                return "Minimum 3 Character Password"
            }
//            if (!passwordText.matches(".*[A-Z].*".toRegex())) {
//                return "Must Contain 1 Upper-case Character"
//            }
//            if (!passwordText.matches(".*[a-z].*".toRegex())) {
//                return "Must Contain 1 Lower-case Character"
//            }
//            if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
//                return "Must Contain 1 Special Character (@#\$%^&+=)"
//            }
            return null
        }

        fun validUser(userText: String): String? {
            if (userText.length < 3) {
                return "Minimum 3 Character Username"
            }
//            if (userText.matches(".*[@#\$%^&+=].*".toRegex())) {
//                return "Must Contain 1 Special Character (@#\$%^&+=)"
//            }
            return null
        }

        fun validName(userText: String): String? {
            if (userText.length < 3) {
                return "Minimum 3 Character Username"
            }
//            if (!userText.matches(".*[@#\$%^&+=].*".toRegex())) {
//                return "Must Contain 1 Special Character (@#\$%^&+=)"
//            }
            return null
        }

    }
}


