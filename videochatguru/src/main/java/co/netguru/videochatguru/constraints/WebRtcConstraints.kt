package co.netguru.videochatguru.constraints

/**
 * Container class for WebRTC constraints management.
 */
open class WebRtcConstraints<T : WebRtcConstraint<E>, E> {

    private val mandatory: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()
    private val optional: MutableMap<WebRtcConstraint<E>, E> = mutableMapOf()

    internal val mandatoryKeyValuePairs
        get() = toKeyValuePairs(mandatory)

    internal val optionalKeyValuePairs
        get() = toKeyValuePairs(optional)


    /**
     * Adds all constraints. If constraints are duplicated value from inserted collection will be used.
     * @see [addAll]
     */
    operator fun plusAssign(other: WebRtcConstraints<T, E>) = addAll(other)

    /**
     * Adds mandatory constraint. If constraint is already present new value will be used.
     */
    fun addMandatoryConstraint(constraint: T, value: E) {
        mandatory.put(constraint, value)
    }

    /**
     * Adds optional constraint. If constraint is already present new value will be used.
     */
    fun addOptionalConstraint(constraint: T, value: E) {
        optional.put(constraint, value)
    }

    /**
     * Adds all constraints. If constraints are duplicated value from inserted collection will be used.
     */
    fun addAll(other: WebRtcConstraints<T, E>) {
        mandatory.putAll(other.mandatory)
        optional.putAll(other.optional)
    }

    private fun toKeyValuePairs(constraintsMap: Map<WebRtcConstraint<E>, E>) = constraintsMap.map { (constraint, enabled) ->
        constraint.toKeyValuePair(enabled)
    }
}