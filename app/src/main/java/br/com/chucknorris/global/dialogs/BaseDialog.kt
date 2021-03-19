package br.com.chucknorris.global.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * @author R/GA
 */
abstract class BaseDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    open fun show(fragmentActivity: FragmentActivity): BaseDialog {
        val ft = fragmentActivity.supportFragmentManager.beginTransaction()
        ft.add(this, fragmentActivity.javaClass.simpleName)
        ft.commitAllowingStateLoss()
        return this
    }

    override fun dismiss() {
        super.dismissAllowingStateLoss()
    }
}