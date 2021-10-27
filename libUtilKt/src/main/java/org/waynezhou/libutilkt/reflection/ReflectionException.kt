package org.waynezhou.libutilkt.reflection

import java.lang.Exception

class ReflectionException(ex: Exception) :
    Exception("Something went wrong which may be caused by reflection.", ex)
