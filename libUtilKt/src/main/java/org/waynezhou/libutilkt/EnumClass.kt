package org.waynezhou.libutilkt

import java.util.*

@Suppress("LeakingThis")
abstract class EnumClass<T: Any, TEnumClass:EnumClass<T,TEnumClass>> protected constructor(
    private val clz: Class<T>,
    val identifier: T,
    val statement: String
) {
    companion object{
        private val map: MutableMap<Any, Any> = mutableMapOf()
    }
    init {
        map[identifier] = this
    }
    override fun hashCode(): kotlin.Int {
        return identifier.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != this.javaClass) return false
        return identifier == (other as? EnumClass<*,*>)?.identifier
    }

    override fun toString(): String {
        return statement
    }

    abstract class Int(statement: String, identifier: kotlin.Int) :
        EnumClass<kotlin.Int, Int>(kotlin.Int::class.java, identifier, statement){
            constructor(statement: String): this(statement, statement.hashCode())
        }

}
