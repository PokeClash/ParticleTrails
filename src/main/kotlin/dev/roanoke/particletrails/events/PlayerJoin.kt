package dev.roanoke.particletrails.events

import dev.roanoke.particletrails.ParticleTrails
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler

class PlayerJoin: ServerPlayConnectionEvents.Join {
    override fun onPlayReady(handler: ServerPlayNetworkHandler?, sender: PacketSender?, server: MinecraftServer?) {
        if (handler != null) {
            val trail = ParticleTrails.permissionManager.getActiveTrail(handler.player)
            if (trail != null) {
                ParticleTrails.playerTrail[handler.player.uuid] = trail
            }
        }
    }
}