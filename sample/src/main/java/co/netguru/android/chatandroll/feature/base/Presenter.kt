package co.netguru.android.chatandroll.feature.base


interface Presenter<T : MvpView> {

    fun attachView(mvpView: T)

    fun detachView()

    fun getView(): T?

}