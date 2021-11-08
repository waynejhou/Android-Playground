package org.waynezhou.libutilkt.enum



@Suppress("LeakingThis", "MemberVisibilityCanBePrivate")
abstract class EnumClass<T: Any, TEnumClass:EnumClass<T,TEnumClass>> protected constructor(
    private val clz: Class<T>,
    val identifier: T,
    val statement: String
) {
    companion object{
        private val maps: MutableMap<Class<EnumClass<*,*>>, MutableMap<Any, Any>> = mutableMapOf()
        @Suppress("UNCHECKED_CAST")
        fun <T:Any, TEnumClass:EnumClass<T, TEnumClass>> getEnumMap(clz:Class<EnumClass<T, TEnumClass>>): MutableMap<T, TEnumClass>? {
            return maps[clz as Class<EnumClass<*,*>>] as? MutableMap<T, TEnumClass>
        }
    }
    init {
        val clzMap = maps.getOrDefault(this.javaClass, mutableMapOf())
        clzMap[identifier] = this
        maps[javaClass] = clzMap
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
