package co.netguru.android.chatandroll.feature.base

import android.os.Bundle


abstract class BaseMvpActivity<T : MvpView, out P : Presenter<T>> : BaseActivity() {

    private lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachViewToPresenter()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachViewToPresenter() {
        presenter = retrievePresenter()
        presenter.attachView(this as T)
    }

    abstract fun retrievePresenter(): P

    fun getPresenter(): P = presenter
}