package co.netguru.android.chatandroll.feature.base


abstract class BasePresenter<T : MvpView> : Presenter<T> {

    //ToDo 16.08.2017 can be reverted to old form when https://youtrack.jetbrains.com/issue/KT-19306 is fixed
    private var mvpView: T? = null

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
    }

    override fun getView() = mvpView

}