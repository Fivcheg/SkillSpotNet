package ru.netology.skillspotnet.validation

class ValidationUser {
    companion object {
        fun validPassword(passwordText: String): String? {
            if (passwordText.length < 3) {
                return "Minimum 3 Character Password"
            }
            return null
        }

        fun validUser(userText: String): String? {
            if (userText.length < 3) {
                return "Minimum 3 Character Username"
            }
            return null
        }

        fun validName(userText: String): String? {
            if (userText.length < 3) {
                return "Minimum 3 Character Username"
            }
            return null
        }

        fun validPasswordRepeat(passwordRepeatText: String, password: String): String?{
            if (passwordRepeatText.hashCode() != password.hashCode()) {
                return "The entered passwords are not equal"
            }
            return null
        }

    }
}


