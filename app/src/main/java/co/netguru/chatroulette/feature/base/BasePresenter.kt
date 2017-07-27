package co.netguru.chatroulette.feature.base


abstract class BasePresenter<T : MvpView> : Presenter<T> {

    protected var mvpView: T? = null
        private set

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
    }
}