package net.xpressdev.particletrails.managers

import net.xpressdev.particletrails.ParticleTrails
import net.xpressdev.particletrails.utils.Trail
import net.xpressdev.particletrails.utils.TrailPack
import net.xpressdev.particletrails.utils.Utils
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.query.Flag
import net.luckperms.api.query.QueryOptions
import net.minecraft.server.network.ServerPlayerEntity

class PermissionManager {

    private fun getLuckPermsUser(player: ServerPlayerEntity): User {
        return ParticleTrails.api.getPlayerAdapter(ServerPlayerEntity::class.java).getUser(player)
    }

    private fun hasPermission(player: ServerPlayerEntity, permission: String): Boolean {
        val user = getLuckPermsUser(player)
        return user.cachedData.permissionData.checkPermission(permission).asBoolean()
    }

    fun canUseTrail(player: ServerPlayerEntity, trail: Trail): Boolean {
        if (player.hasPermissionLevel(2))
            return true

        return hasPermission(player, "particletrails.pack.${trail.packId}.trail.${trail.name}")
    }

    fun canUsePack(player: ServerPlayerEntity, trail: Trail): Boolean {
        if (player.hasPermissionLevel(2))
            return true

        return hasPermission(player, "particletrails.pack.${trail.packId}.*")
    }

    fun canSeePack(player: ServerPlayerEntity, pack: TrailPack): Boolean {
        if (player.hasPermissionLevel(2))
            return true

        val user = getLuckPermsUser(player)
        user.nodes.forEach { node ->
            if (node.key.contains("particletrails.pack.${pack.name}")) {
                return true
            }
        }

        user.resolveInheritedNodes((QueryOptions.nonContextual())).forEach {
            if (it.key.contains("particletrails.pack.${pack.name}")) {
                return true
            }
        }

        return false
    }

    fun givePack(player: ServerPlayerEntity?, pack: TrailPack) {
        if (player != null) {
            ParticleTrails.api.userManager.modifyUser(player.uuid) {
                it.data().add(Node.builder("particletrails.pack.${pack.name}.trail.*").build())
            }
        }
    }

    fun removePack(player: ServerPlayerEntity?, pack: TrailPack) {
        if (player != null) {
            ParticleTrails.api.userManager.modifyUser(player.uuid) {
                it.data().remove(Node.builder("particletrails.pack.${pack.name}.trail.*").build())
            }
        }
    }

    fun giveTrail(player: ServerPlayerEntity?, trail: Trail) {
        if (player != null) {
            ParticleTrails.api.userManager.modifyUser(player.uuid) {
                it.data().add(Node.builder("particletrails.pack.${trail.packId}.trail.${trail.name}").build())
            }
        }
    }

    fun removeTrail(player: ServerPlayerEntity?, trail: Trail) {
        if (player != null) {
            ParticleTrails.api.userManager.modifyUser(player.uuid) {
                it.data().remove(Node.builder("particletrails.pack.${trail.packId}.trail.${trail.name}").build())
            }
        }
    }

    fun getActiveTrail(player: ServerPlayerEntity): Trail? {
        val user = getLuckPermsUser(player)
        user.nodes.forEach { node ->
            if (node.key.contains("particletrails.active.")) {
                return Utils.getTrailByName(node.key.split(".")[2])
            }
        }
        return null
    }

    fun setActiveTrail(player: ServerPlayerEntity, trail: Trail) {
        ParticleTrails.api.userManager.modifyUser(player.uuid) {
            it.data().add(Node.builder("particletrails.active.${trail.name}").build())
        }
    }

    fun removeActiveTrail(player: ServerPlayerEntity) {
        val activeTrail = getActiveTrail(player)
        if (activeTrail != null)
            ParticleTrails.api.userManager.modifyUser(player.uuid) {
                it.data().remove(Node.builder("particletrails.active.${activeTrail.name}").build())
            }
    }

}