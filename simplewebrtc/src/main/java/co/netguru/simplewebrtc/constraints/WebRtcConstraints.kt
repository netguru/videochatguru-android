package co.netguru.simplewebrtc.constraints


class WebRtcConstraints<T : WebRtcConstraint<E>, E> {

    private val mandatory: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()
    private val optional: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()

    val mandatoryKeyValuePairs
        get() = toKeyValuePairs(mandatory)

    val optionalKeyValuePairs
        get() = toKeyValuePairs(optional)


    operator fun plusAssign(other: WebRtcConstraints<T, E>) = addAll(other)

    fun addMandatoryConstraint(constraint: T, value: E) {
        mandatory.put(constraint, value)
    }

    fun addOptionalConstraint(constraint: T, value: E) {
        optional.put(constraint, value)
    }

    fun addAll(other: WebRtcConstraints<T, E>) {
        mandatory.putAll(other.mandatory)
        optional.putAll(other.optional)
    }

    private fun toKeyValuePairs(constraintsMap: Map<WebRtcConstraint<E>, E>) = constraintsMap.map { (constraint, enabled) ->
        constraint.toKeyValuePair(enabled)
    }
}