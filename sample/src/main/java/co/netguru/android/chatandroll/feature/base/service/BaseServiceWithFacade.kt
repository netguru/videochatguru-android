package co.netguru.android.chatandroll.feature.base.service

import android.app.Service


abstract class BaseServiceWithFacade<T : ServiceFacade, out C : ServiceController<T>> : Service() {

    private lateinit var serviceController: C

    override fun onCreate() {
        super.onCreate()
        attachServiceToController()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceController.detachService()
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachServiceToController() {
        serviceController = retrieveController()
        serviceController.attachService(this as T)
    }

    abstract fun retrieveController(): C
}