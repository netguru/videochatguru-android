package co.netguru.chatroulette.feature.base.service


interface ServiceController<T : ServiceFacade> {

    fun attachService(service: T)

    fun detachService()

    fun getService(): T?

}