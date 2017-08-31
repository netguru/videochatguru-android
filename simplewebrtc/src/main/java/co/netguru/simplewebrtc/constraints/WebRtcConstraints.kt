package co.netguru.simplewebrtc.constraints


class WebRtcConstraints<T : WebRtcConstraint<E>, E> {

    private val mandatory: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()
    private val optional: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()

    fun addMandatoryConstraint(constraint: T, value: E) {
        mandatory.put(constraint, value)
    }

    fun addOptionalConstraint(constraint: T, value: E) {
        optional.put(constraint, value)
    }

    operator fun plusAssign(other: WebRtcConstraints<T, E>) {
        addAll(other)
    }

    fun addAll(other: WebRtcConstraints<T, E>) {
        mandatory.putAll(other.mandatory)
        optional.putAll(other.optional)
    }

    fun getMandatoryKeyValuePairs() = toKeyValuePairs(mandatory)

    fun getOptionalKeyValuePairs() = toKeyValuePairs(optional)

    private fun toKeyValuePairs(map: Map<WebRtcConstraint<E>, E>) = map.map { (constraint, enabled) ->
        constraint.toKeyValuePair(enabled)
    }
}