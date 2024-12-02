package com.dicoding.picodiploma.loginwithanimation.view.textvalidation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet
): AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        setOnTouchListener(this)
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s)) {
                    setError("Format email tidak valid")
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }

        })
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val drawableEndX = width - paddingEnd
            val drawableStartX = drawableEndX - compoundDrawables[2].intrinsicWidth

            if (event.x > drawableStartX && event.x < drawableEndX){
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}