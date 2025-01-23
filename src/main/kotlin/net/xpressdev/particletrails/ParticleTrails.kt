package net.xpressdev.particletrails

import net.xpressdev.particletrails.events.PlayerJoin
import net.xpressdev.particletrails.events.PlayerLeave
import net.xpressdev.particletrails.managers.PermissionManager
import net.xpressdev.particletrails.utils.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ParticleTrails : ModInitializer {

    companion object {
        private lateinit var _serverInstance: MinecraftServer

        val serverInstance: MinecraftServer
            get() = _serverInstance

        var LOGGER: Logger = LoggerFactory.getLogger("EntityCommands")

        lateinit var api: LuckPerms
        lateinit var adventure: FabricServerAudiences
        val mm: MiniMessage = MiniMessage.miniMessage()

        val permissionManager: PermissionManager = PermissionManager()

        val trailPacks: MutableList<TrailPack> = mutableListOf()
        val playerTrail: MutableMap<UUID, Trail> = mutableMapOf()

        val config = Config()
    }

    override fun onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register { server: MinecraftServer ->
            _serverInstance = server
            adventure = FabricServerAudiences.of(server)
            try {
                api = LuckPermsProvider.get()
            } catch (e: Exception) {
                LOGGER.error("LuckPerms API not found")
            }
        }

        net.xpressdev.particletrails.commands.Trails(this)

        ServerPlayConnectionEvents.JOIN.register(PlayerJoin())
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerLeave())

        var tick = 0
        ServerTickEvents.START_SERVER_TICK.register {
            if (tick >= 20)
                tick = 0
            playerTrail.forEach {(uuid, trail) ->
                val player = Utils.getPlayerByUUID(uuid)?: return@register
                Utils.spawnParticles(player, trail, tick)
            }
            tick++;
        }
    }

}