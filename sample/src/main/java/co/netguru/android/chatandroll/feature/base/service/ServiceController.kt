package co.netguru.android.chatandroll.feature.base.service


interface ServiceController<T : ServiceFacade> {

    fun attachService(service: T)

    fun detachService()

    fun getService(): T?

}