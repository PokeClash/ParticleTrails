package net.xpressdev.particletrails.events

import net.xpressdev.particletrails.ParticleTrails
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler

class PlayerLeave: ServerPlayConnectionEvents.Disconnect {
    override fun onPlayDisconnect(handler: ServerPlayNetworkHandler?, server: MinecraftServer?) {
        if (handler != null) {
            if (ParticleTrails.playerTrail.containsKey(handler.player.uuid)) {
                ParticleTrails.playerTrail.remove(handler.player.uuid)
            }
        }
    }
}