package dev.roanoke.particletrails.utils

import dev.roanoke.particletrails.ParticleTrails
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class Utils {
    companion object {
        fun broadcast(message: String) {
            val server = ParticleTrails.serverInstance

            for (player in server.playerManager.playerList) {
                if (player is ServerPlayerEntity) {
                    player.sendMessage(Text.literal(message), false)
                }
            }
        }

        fun getPlayerByUUID(uuid: UUID): ServerPlayerEntity? {
            val server = ParticleTrails.serverInstance
            val playerManager = server.playerManager

            return playerManager.getPlayer(uuid)
        }

        fun getPlayerByName(name: String): ServerPlayerEntity? {
            val server = ParticleTrails.serverInstance
            val playerManager = server.playerManager

            return playerManager.getPlayer(name)
        }

        fun getAllPlayerNames(): Array<out String>? {
            val server = ParticleTrails.serverInstance
            val playerManager = server.playerManager

            return playerManager.playerNames
        }

        fun createItem(trail: Trail): ItemStack {
            val item = ItemStack(Items.PAPER)
            item.setCustomName(trail.getDisplayName())

            if (trail.description != "") {
                val lore = mutableListOf(Text.literal("§f" + trail.description))
                val nbt = item.orCreateNbt
                val displayNbt = item.getOrCreateSubNbt("display")
                val nbtLore = NbtList()
                for (line in lore)
                    nbtLore.add(NbtString.of(Text.Serializer.toJson(line)))
                displayNbt.put("Lore", nbtLore)
                nbt.put("display", displayNbt)
                item.nbt = nbt
            }

            return item
        }

        fun parseMM(string: String): Text {
            return ParticleTrails.adventure.toNative(
                ParticleTrails.mm.deserialize(string)
            )
        }

        fun giveTrail(player: ServerPlayerEntity, trail: String) {
            ParticleTrails.permissionManager.giveTrail(player, trail)
        }

        fun applyTrail(player: ServerPlayerEntity, trail: Trail) {
            ParticleTrails.playerTrail[player.uuid] = trail
        }

        fun clearTrail(player: ServerPlayerEntity) {
            ParticleTrails.playerTrail.remove(player.uuid)
        }

        fun getParticleFromString(particle: String): ParticleType<*> {
            return when (particle) {
                "block" -> ParticleTypes.BLOCK
                "falling_dust" -> ParticleTypes.FALLING_DUST
                "item" -> ParticleTypes.ITEM
                "vibration" -> ParticleTypes.VIBRATION
                "ambient_entity_effect" -> ParticleTypes.AMBIENT_ENTITY_EFFECT
                "explosion" -> ParticleTypes.EXPLOSION
                "explosion_emitter" -> ParticleTypes.EXPLOSION_EMITTER
                "flame" -> ParticleTypes.FLAME
                "soul_fire_flame" -> ParticleTypes.SOUL_FIRE_FLAME
                "soul" -> ParticleTypes.SOUL
                "firework" -> ParticleTypes.FIREWORK
                "heart" -> ParticleTypes.HEART
                else -> ParticleTypes.AMBIENT_ENTITY_EFFECT
            }
        }

        fun spawnParticles(player: ServerPlayerEntity, trail: Trail, tick: Int = 0) {
            val particle = trail.particleType()
            if (trail.trailType == TrailType.DEFAULT) {
                for (i in 0..3) {
                    spawnParticle(player.pos, player.world as ServerWorld, particle)
                }
            } else if (trail.trailType == TrailType.HALO) {
                val center = player.pos
                val radius = .3

                for (i in 0 until 360 step 40) {
                    val angleRad = Math.toRadians(i.toDouble())
                    val offsetX = radius * cos(angleRad)
                    val offsetZ = radius * sin(angleRad)
                    val newPos = Vec3d(center.x + offsetX, center.y + player.height + .3, center.z + offsetZ)

                    spawnParticle(newPos, player.world as ServerWorld, particle)
                }
            } else if (trail.trailType == TrailType.ROTATING) {
                if (tick % 4 != 0) return
                val center = player.pos
                val radius = .4 // Adjust the radius as needed
                val speed = 12.5 // Adjust the speed of rotation as needed

                val worldTick = player.world.time

                val angleRad = Math.toRadians((worldTick * speed) % 360)
                val offsetX = radius * cos(angleRad)
                val offsetZ = radius * sin(angleRad)
                val newPos = Vec3d(center.x + offsetX, center.y + player.height + .3, center.z + offsetZ)

                spawnParticle(newPos, player.world as ServerWorld, particle)
            }
        }

        private fun spawnParticle(pos: Vec3d, world: ServerWorld, particle: ParticleEffect) {
            ParticleS2CPacket(particle, true, pos.x, pos.y, pos.z, 0.0f, 0.0f, 0.0f, 0.0f, 1).also {
                world.server.playerManager.sendToAround(
                    null,
                    pos.x,
                    pos.y,
                    pos.z,
                    64.0,
                    world.registryKey,
                    it
                )
            }
        }
    }
}