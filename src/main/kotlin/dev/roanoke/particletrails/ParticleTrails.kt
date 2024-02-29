package dev.roanoke.particletrails

import dev.roanoke.particletrails.commands.Trails
import dev.roanoke.particletrails.managers.PermissionManager
import dev.roanoke.particletrails.utils.Trail
import dev.roanoke.particletrails.utils.TrailCategory
import dev.roanoke.particletrails.utils.TrailType
import dev.roanoke.particletrails.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.minecraft.server.MinecraftServer
import org.joml.Vector3f
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

        val trailCategories: MutableList<TrailCategory> = mutableListOf()
//        val trails: MutableList<Trail> = mutableListOf()
        val playerTrail: MutableMap<UUID, Trail> = mutableMapOf()
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

        Trails(this)
        val default = TrailCategory("default", "Default Trails", "<white><bold>Default", mutableListOf(), TrailType.DEFAULT)
        default.addTrail(Trail("default_fireworks", "<white><bold>Fireworks", "Default Firework Trail", TrailType.DEFAULT, "firework"))
        default.addTrail(Trail("default_heart", "<red><bold>Heart", "Default Heart Trail", TrailType.DEFAULT, "heart"))
        val rotating = TrailCategory("rotating", "Rotating Trails", "<white><bold>Rotating", mutableListOf(), TrailType.ROTATING)
        rotating.addTrail(Trail("rotating_heart", "<red><bold>Cloud 9", "Heart rotating above your head", TrailType.ROTATING, "heart"))
        rotating.addTrail(Trail("rotating_firework", "<white><bold>Spark", "Firework Sparks rotating above your head", TrailType.ROTATING, "firework"))
        val halo = TrailCategory("halo", "Halo Trails", "<white><bold>Halo", mutableListOf(), TrailType.HALO)
        halo.addTrail(Trail("halo_red_dust", "<red><bold>Red Halo", "Red Halo above your head", TrailType.HALO, "dust", Vector3f(1f, 0f, 0f)))
        halo.addTrail(Trail("halo_white_dust", "<white><bold>White Halo", "White Halo above your head", TrailType.HALO, "dust", Vector3f(1f, 1f, 1f)))
        halo.addTrail(Trail("halo_yellow_dust", "<yellow><bold>Yellow Halo", "Yellow Halo above your head", TrailType.HALO, "dust", Vector3f(1f, 1f, 0f)))
        trailCategories.add(default)
        trailCategories.add(rotating)
        trailCategories.add(halo)

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