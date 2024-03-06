package dev.roanoke.particletrails.utils

import net.minecraft.text.Text

enum class TrailType {
    DEFAULT, HALO, ROTATING, SPIRAL, DUAL_SPIRAL, WISP, AURA, CLOUD, ORBIT;

    companion object {
        fun fromString(value: String): TrailType {
            return when(value.lowercase().trim()) {
                "default" -> DEFAULT
                "halo" -> HALO
                "rotating" -> ROTATING
                "spiral" -> SPIRAL
                "dual_spiral" -> DUAL_SPIRAL
                "dual spiral" -> DUAL_SPIRAL
                "wisp" -> WISP
                "aura" -> AURA
                "cloud" -> CLOUD
                "orbit" -> ORBIT
                else -> DEFAULT
            }
        }
    }

}