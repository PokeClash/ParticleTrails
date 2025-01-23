package net.xpressdev.particletrails.utils

import net.minecraft.particle.*
import net.minecraft.text.Text
import org.joml.Vector3f

class Trail(val name: String, private val display: String, val description: String, val trailType: TrailType, private val particleType: String? = null, private val colour: Vector3f? = null, val packId: String = "default") {

    fun getDisplayName(): Text {
        return Utils.parseMM(display)
    }

    fun particleType(): ParticleEffect {
        return if (colour != null) {
            DustParticleEffect(colour, 1.0f)
        } else {
            Utils.getParticleFromString(particleType!!) as ParticleEffect
        }
    }

}