package net.xpressdev.particletrails.utils

import net.minecraft.text.Text

class TrailPack(val name: String, val description: String, private val display: String, val trails: MutableList<Trail>) {

    fun getTrailByName(name: String): Trail? {
        return trails.firstOrNull { it.name == name }
    }

    fun addTrail(trail: Trail) {
        trails.add(trail)
    }

    fun getDisplayName(): Text {
        return Utils.parseMM(display)
    }
}