package co.netguru.chatroulette.feature.base.service

import android.app.Service


abstract class BaseServiceWithFacade : Service() {
    override fun onCreate() {
        super.onCreate()
        attachServiceToController()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachService()
    }

    private fun detachService() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachServiceToController() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}