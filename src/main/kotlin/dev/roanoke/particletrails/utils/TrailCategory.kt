package dev.roanoke.particletrails.utils

class TrailCategory(val name: String, private val trails: MutableList<Trail>, val trailType: TrailType) {

    fun getTrailByName(name: String): Trail? {
        return trails.firstOrNull { it.name == name }
    }

    fun addTrail(trail: Trail) {
        trails.add(trail)
    }
}