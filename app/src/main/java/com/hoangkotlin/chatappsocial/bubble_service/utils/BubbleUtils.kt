package com.hoangkotlin.chatappsocial.bubble_service.utils

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.view.View
import kotlin.math.sqrt

internal fun View.getPosition(): Point {
    val arr = IntArray(2)
    this.getLocationOnScreen(arr)
    return Point(arr.first(), arr.last())
}

data class Circle(val center: PointF, val radius: Float)

internal fun View.getBound(): Rect {
    val position = getPosition()
    return Rect(
        position.x,
        position.y,
        position.x + width,
        position.y + height
    )
}

internal fun View.getCircleBound(padding: Float = 0f): Circle {
    val position = getPosition()
    val centerX = position.x + width / 2f
    val centerY = position.y + height / 2f
    val radius = width / 2f // Assuming the view is a perfect circle
    return Circle(PointF(centerX, centerY), radius + padding)
}


internal fun PointF.distanceTo(other: PointF): Float {
    return sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y))
}

internal fun Circle.isCollidingWith(other: Circle): Boolean {
    val distanceBetweenCenters = center.distanceTo(other.center)
    return distanceBetweenCenters < (this.radius + other.radius)
}