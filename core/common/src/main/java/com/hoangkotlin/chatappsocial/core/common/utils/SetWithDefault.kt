package com.hoangkotlin.chatappsocial.core.common.utils

class SetWithDefault<T>(
    private val default: T,
    private val maxSizeExcludeDefault: Int
) {

    private val _items = LinkedHashSet<T>()

    init {
        _items.add(default)
    }

    val items: List<T>
        get() = _items.toList()

    val itemWithoutDefault: List<T>
        get() = _items.toList()


    fun add(item: T) {
        synchronized(_items) {
            if (_items.add(item) && _items.size > maxSizeExcludeDefault) {
                val iterator = _items.iterator()
                iterator.remove()
            }
        }
    }

    fun remove(item: T) {
        synchronized(_items) {
            if (item == default) {
                return
            }
            _items.remove(item)
        }
    }


}