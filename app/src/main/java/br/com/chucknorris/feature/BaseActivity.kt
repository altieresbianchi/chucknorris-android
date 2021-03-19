package br.com.chucknorris.feature

import androidx.appcompat.app.AppCompatActivity
import br.com.chucknorris.global.dialogs.LoadingDialog

open class BaseActivity : AppCompatActivity() {
    private var loadingDialog: LoadingDialog? = null

    fun showLoading() {
        this.showLoading(false)
    }

    fun showLoading(isCancelable: Boolean) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
            loadingDialog?.isCancelable = isCancelable

            loadingDialog?.show(supportFragmentManager, this.javaClass.simpleName)
        }
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null;
    }
}
