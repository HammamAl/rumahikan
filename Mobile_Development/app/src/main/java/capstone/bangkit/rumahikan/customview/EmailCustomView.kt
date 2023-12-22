package capstone.bangkit.rumahikan.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import capstone.bangkit.rumahikan.R

class EmailCustomView : AppCompatEditText, View.OnFocusChangeListener {

    var isEmailValid = false
    private lateinit var emailSame: String
    private var isEmailHasTaken = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {

        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
                if (isEmailHasTaken) {
                    validateEmailHasTaken()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validateEmail()
            if (isEmailHasTaken) {
                validateEmailHasTaken()
            }
        }
    }

    private fun validateEmail() {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!isEmailValid) {
            resources.getString(R.string.emailFormatWrong)
        } else {
            null
        }
    }

    private fun validateEmailHasTaken() {
        error = if (isEmailHasTaken && text.toString().trim() == emailSame) {
            resources.getString(R.string.emailTaken)
        } else {
            null
        }
    }

    fun setErrorMessage(message: String, email: String) {
        emailSame = email
        isEmailHasTaken = true
        error = if (text.toString().trim() == emailSame) {
            message
        } else {
            null
        }
    }
}