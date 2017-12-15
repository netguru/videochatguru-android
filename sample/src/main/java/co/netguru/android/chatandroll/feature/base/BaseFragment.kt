package co.netguru.android.chatandroll.feature.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.chatandroll.R
import co.netguru.android.chatandroll.common.extension.getColorCompat

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(getLayoutId(), container, false)

    @LayoutRes
    abstract fun getLayoutId(): Int

    fun showSnackbarMessage(@StringRes resId: Int, @BaseTransientBottomBar.Duration duration: Int) {
        view?.let {
            val snackbar = Snackbar.make(it, resId, duration)
            val layout = snackbar.view as Snackbar.SnackbarLayout
            layout.setBackgroundColor(context.getColorCompat(R.color.transparent_black))
            snackbar.show()
        }
    }
}