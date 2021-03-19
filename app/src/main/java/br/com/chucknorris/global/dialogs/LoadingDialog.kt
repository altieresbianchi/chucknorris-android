package br.com.chucknorris.global.dialogs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentActivity
import br.com.chucknorris.R

open class LoadingDialog : BaseDialog() {
    private var cancelable = false
    private var cancelableOnTouchOutSide = false
    private var isShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Dialog_NoActionBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_loading, container)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelableOnTouchOutSide)
        dialog?.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK != cancelable }
        return view
    }

    open fun isShowing(): Boolean {
        return isShowing
    }

    override fun show(fragmentActivity: FragmentActivity): BaseDialog {
        isShowing = true
        return super.show(fragmentActivity)
    }

    open fun show(
        fragmentActivity: FragmentActivity, cancelable: Boolean, cancelableOnTouchOutSide: Boolean
    ): BaseDialog? {
        this.isCancelable = cancelable
        setCancelableOnTouchOutSide(cancelableOnTouchOutSide)
        return this.show(fragmentActivity)
    }

    override fun dismiss() {
        isShowing = false
        super.dismiss()
    }

    override fun setCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
    }

    open fun setCancelableOnTouchOutSide(cancelableOnTouchOutSide: Boolean) {
        this.cancelableOnTouchOutSide = cancelableOnTouchOutSide
    }
}