package dev.roanoke.particletrails.managers

import dev.roanoke.particletrails.ParticleTrails
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.minecraft.server.network.ServerPlayerEntity

class PermissionManager {

    fun getLuckPermsUser(player: ServerPlayerEntity): User {
        return ParticleTrails.api.getPlayerAdapter(ServerPlayerEntity::class.java).getUser(player)
    }

    fun hasPermission(player: ServerPlayerEntity, permission: String): Boolean {
        val user = getLuckPermsUser(player)
        return user.cachedData.permissionData.checkPermission(permission).asBoolean()
    }

    fun canUseTrail(player: ServerPlayerEntity, trail: String): Boolean {
        return hasPermission(player, "particletrails.trail.$trail")
    }

    fun giveTrail(player: ServerPlayerEntity, trail: String) {
        ParticleTrails.api.userManager.modifyUser(player.uuid) {
            it.data().add(Node.builder("particletrails.trail.$trail").build())
        }
    }

    fun removeTrail(player: ServerPlayerEntity, trail: String) {
        ParticleTrails.api.userManager.modifyUser(player.uuid) {
            it.data().remove(Node.builder("particletrails.trail.$trail").build())
        }
    }

}