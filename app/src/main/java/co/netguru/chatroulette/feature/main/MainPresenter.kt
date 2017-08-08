package co.netguru.chatroulette.feature.main

import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MainPresenter @Inject constructor() : BasePresenter<MainView>() {

    private val disposables = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        disposables.dispose()
    }

}