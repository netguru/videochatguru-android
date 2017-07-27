package co.netguru.chatroulette.feature.base


interface Presenter<in T : MvpView> {

    fun attachView(mvpView: T)

    fun detachView()

}