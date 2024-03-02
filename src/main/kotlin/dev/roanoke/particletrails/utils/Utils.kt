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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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

        fun createItem(player: ServerPlayerEntity, category: TrailPack): ItemStack {
            var size = 0
            category.trails.forEach {
                if (ParticleTrails.permissionManager.canUseTrail(player, it)) {
                    size++
                }
            }

            val item = ItemStack(Items.PAPER, size)
            item.setCustomName(category.getDisplayName())

            if (category.description != "") {
                val lore = mutableListOf(Text.literal("§f" + category.description))
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

        fun giveTrail(player: ServerPlayerEntity?, trail: Trail) {
            ParticleTrails.permissionManager.giveTrail(player, trail)
        }

        fun removeTrail(player: ServerPlayerEntity?, trail: Trail) {
            ParticleTrails.permissionManager.removeTrail(player, trail)
        }

        fun applyTrail(player: ServerPlayerEntity, trail: Trail) {
            ParticleTrails.permissionManager.removeActiveTrail(player)
            ParticleTrails.playerTrail[player.uuid] = trail
            ParticleTrails.permissionManager.setActiveTrail(player, trail)
        }

        fun clearTrail(player: ServerPlayerEntity) {
            ParticleTrails.playerTrail.remove(player.uuid)
            ParticleTrails.permissionManager.removeActiveTrail(player)
        }

        fun getPackByName(name: String): TrailPack? {
            return ParticleTrails.trailCategories.find { it.name == name }
        }

        fun getTrailByName(name: String): Trail? {
            return ParticleTrails.trailCategories.flatMap { it.trails }.find { it.name == name }
        }

        fun getParticleFromString(particle: String): ParticleType<*> {
            return when (particle) {
                "ambient_entity_effect" -> ParticleTypes.AMBIENT_ENTITY_EFFECT
                "angry_villager" -> ParticleTypes.ANGRY_VILLAGER
                "bubble" -> ParticleTypes.BUBBLE
                "bubble_column_up" -> ParticleTypes.BUBBLE_COLUMN_UP
                "bubble_pop" -> ParticleTypes.BUBBLE_POP
                "campfire_cosy_smoke" -> ParticleTypes.CAMPFIRE_COSY_SMOKE
                "cloud" -> ParticleTypes.CLOUD
                "composter" -> ParticleTypes.COMPOSTER
                "critical_hit" -> ParticleTypes.CRIT
                "current_down" -> ParticleTypes.CURRENT_DOWN
                "damage_indicator" -> ParticleTypes.DAMAGE_INDICATOR
                "dolphin" -> ParticleTypes.DOLPHIN
                "dragon_breath" -> ParticleTypes.DRAGON_BREATH
                "dripping_dripstone_lava" -> ParticleTypes.DRIPPING_DRIPSTONE_LAVA
                "dripping_dripstone_water" -> ParticleTypes.DRIPPING_DRIPSTONE_WATER
                "dripping_honey" -> ParticleTypes.DRIPPING_HONEY
                "dripping_lava" -> ParticleTypes.DRIPPING_LAVA
                "dripping_obsidian_tear" -> ParticleTypes.DRIPPING_OBSIDIAN_TEAR
                "dripping_water" -> ParticleTypes.DRIPPING_WATER
                "effect" -> ParticleTypes.EFFECT
                "elder_guardian" -> ParticleTypes.ELDER_GUARDIAN
                "enchant" -> ParticleTypes.ENCHANT
                "enchanted_hit" -> ParticleTypes.ENCHANTED_HIT
                "end_rod" -> ParticleTypes.END_ROD
                "entity_effect" -> ParticleTypes.ENTITY_EFFECT
                "explosion" -> ParticleTypes.EXPLOSION
                "explosion_emitter" -> ParticleTypes.EXPLOSION_EMITTER
                "falling_dust" -> ParticleTypes.FALLING_DUST
                "falling_honey" -> ParticleTypes.FALLING_HONEY
                "falling_lava" -> ParticleTypes.FALLING_LAVA
                "falling_nectar" -> ParticleTypes.FALLING_NECTAR
                "falling_obsidian_tear" -> ParticleTypes.FALLING_OBSIDIAN_TEAR
                "falling_spore_blossom" -> ParticleTypes.FALLING_SPORE_BLOSSOM
                "falling_water" -> ParticleTypes.FALLING_WATER
                "firework" -> ParticleTypes.FIREWORK
                "flame" -> ParticleTypes.FLAME
                "flash" -> ParticleTypes.FLASH
                "happy_villager" -> ParticleTypes.HAPPY_VILLAGER
                "heart" -> ParticleTypes.HEART
                "instant_effect" -> ParticleTypes.INSTANT_EFFECT
                "landing_honey" -> ParticleTypes.LANDING_HONEY
                "landing_lava" -> ParticleTypes.LANDING_LAVA
                "landing_obsidian_tear" -> ParticleTypes.LANDING_OBSIDIAN_TEAR
                "large_smoke" -> ParticleTypes.LARGE_SMOKE
                "lava" -> ParticleTypes.LAVA
                "mycelium" -> ParticleTypes.MYCELIUM
                "nautilus" -> ParticleTypes.NAUTILUS
                "note" -> ParticleTypes.NOTE
                "poof" -> ParticleTypes.POOF
                "portal" -> ParticleTypes.PORTAL
                "rain" -> ParticleTypes.RAIN
                "smoke" -> ParticleTypes.SMOKE
                "sneeze" -> ParticleTypes.SNEEZE
                "snowflake" -> ParticleTypes.SNOWFLAKE
                "soul" -> ParticleTypes.SOUL
                "soul_fire_flame" -> ParticleTypes.SOUL_FIRE_FLAME
                "spit" -> ParticleTypes.SPIT
                "squid_ink" -> ParticleTypes.SQUID_INK
                "sweep_attack" -> ParticleTypes.SWEEP_ATTACK
                "totem_of_undying" -> ParticleTypes.TOTEM_OF_UNDYING
                "underwater" -> ParticleTypes.UNDERWATER
                "vibration" -> ParticleTypes.VIBRATION
                "wax_on" -> ParticleTypes.WAX_ON
                "witch" -> ParticleTypes.WITCH
                "wax_off" -> ParticleTypes.WAX_OFF
                "spore_blossom_air" -> ParticleTypes.SPORE_BLOSSOM_AIR
                else -> ParticleTypes.AMBIENT_ENTITY_EFFECT
            }
        }


        fun spawnParticles(player: ServerPlayerEntity, trail: Trail, tick: Int = 0) {
            val particle = trail.particleType()
            when (trail.trailType) {
                TrailType.DEFAULT -> {
                    if (tick % 4 != 0) return
                    for (i in 0..1) {
                        spawnParticle(player.boundingBox.center, player.world as ServerWorld, particle)
                    }
                }

                TrailType.HALO -> {
                    val center = player.pos
                    val radius = .3

                    for (i in 0 until 360 step 20) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val offsetX = radius * cos(angleRad)
                        val offsetZ = radius * sin(angleRad)
                        val newPos = Vec3d(center.x + offsetX, center.y + player.height + .3, center.z + offsetZ)

                        spawnParticle(newPos, player.world as ServerWorld, particle)
                    }
                }

                TrailType.ROTATING -> {
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

                TrailType.WISP -> {
                    val radius = 1.0
                    val rotationSpeed = 0.1

                    val worldTick = player.world.time
                    val angle = worldTick * rotationSpeed
                    val x = player.x + radius * cos(angle)
                    val y = player.eyeY
                    val z = player.z + radius * sin(angle)

                    val newPos = Vec3d(x, y, z)
                    spawnParticle(newPos, player.world as ServerWorld, particle)
                }

                TrailType.SPIRAL -> {
                    val radius = 1.0
                    val height = player.height + .3 // Height of the spiral
                    val loops = 3 // Number of loops in the spiral
                    val rotationsPerLoop = 1 // Rotations per loop
                    val speed = .3 // Speed of particle movement

                    val worldTick = player.world.time
                    val angle = (worldTick * speed) % (2 * Math.PI * loops * rotationsPerLoop)

                    val x = player.x + radius * cos(angle)
                    val y = player.y + height * angle / (2 * Math.PI * loops * rotationsPerLoop)
                    val z = player.z + radius * sin(angle)

                    val newPos = Vec3d(x, y, z)
                    spawnParticle(newPos, player.world as ServerWorld, particle)
                }

                TrailType.DUAL_SPIRAL -> {
                    val radius = 1.0
                    val height = player.height + .3 // Height of the spiral
                    val loops = 3 // Number of loops in the spiral
                    val rotationsPerLoop = 1 // Rotations per loop
                    val speed = 0.3 // Speed of particle movement

                    val worldTick = player.world.time
                    val angle = (worldTick * speed) % (2 * Math.PI * loops * rotationsPerLoop)

                    // Calculate positions for both sides of the player
                    val x1 = player.x + radius * cos(angle)
                    val y1 = player.y + height * angle / (2 * Math.PI * loops * rotationsPerLoop)
                    val z1 = player.z + radius * sin(angle)

                    val x2 = player.x + radius * cos(angle + PI)
                    val y2 = player.y + height * angle / (2 * Math.PI * loops * rotationsPerLoop)
                    val z2 = player.z + radius * sin(angle + PI)

                    val newPos1 = Vec3d(x1, y1, z1)
                    val newPos2 = Vec3d(x2, y2, z2)

                    spawnParticle(newPos1, player.world as ServerWorld, particle)
                    spawnParticle(newPos2, player.world as ServerWorld, particle)
                }

                TrailType.AURA -> {
                    val multiplier = 2
                    val bb = player.boundingBox
                    for (i in 0..4) {
                        val offsetX = Random.nextDouble(-bb.xLength * multiplier, bb.xLength * multiplier)
                        val offsetY = Random.nextDouble(-bb.yLength, bb.yLength)
                        val offsetZ = Random.nextDouble(-bb.zLength * multiplier, bb.zLength * multiplier)
                        spawnParticle(
                            player.boundingBox.center,
                            player.world as ServerWorld,
                            particle,
                            Vec3d(offsetX, offsetY, offsetZ)
                        )
                    }
                }

                TrailType.CLOUD -> {
                    for (i in 0..8) {
                        val offsetX = Random.nextDouble(-.5, .5)
                        val offsetY = Random.nextDouble(0.0, 0.6)
                        val offsetZ = Random.nextDouble(-.5, .5)
                        spawnParticle(
                            player.eyePos.add(Vec3d(0.0, 0.5, 0.0)),
                            player.world as ServerWorld,
                            particle,
                            Vec3d(offsetX, offsetY, offsetZ)
                        )
                    }
                }
                TrailType.ORBIT -> {
                    val radius = .8
                    val rotationSpeed = 0.1

                    val worldTick = player.world.time
                    val angle = worldTick * rotationSpeed
                    val angle2 = angle + (2 * PI / 3)
                    val angle3 = angle + (4 * PI / 3)

                    var x = player.x + radius * cos(angle)
                    var y = player.boundingBox.center.y
                    var z = player.z + radius * sin(angle)
                    var newPos = Vec3d(x, y, z)
                    spawnParticle(newPos, player.world as ServerWorld, particle)

                    x = player.x + radius * cos(angle2)
                    y = player.boundingBox.center.y
                    z = player.z + radius * sin(angle2)
                    newPos = Vec3d(x, y, z)
                    spawnParticle(newPos, player.world as ServerWorld, particle)

                    x = player.x + radius * cos(angle3)
                    y = player.boundingBox.center.y
                    z = player.z + radius * sin(angle3)
                    newPos = Vec3d(x, y, z)
                    spawnParticle(newPos, player.world as ServerWorld, particle)
                }
                TrailType.RINGS -> {
                    // TODO
                }
            }
        }

        private fun spawnParticle(
            pos: Vec3d,
            world: ServerWorld,
            particle: ParticleEffect,
            offset: Vec3d = Vec3d.ZERO
        ) {
            ParticleS2CPacket(
                particle,
                true,
                pos.x + offset.x,
                pos.y + offset.y,
                pos.z + offset.z,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1
            ).also {
                world.server.playerManager.sendToAround(
                    null,
                    pos.x + offset.x,
                    pos.y + offset.y,
                    pos.z + offset.z,
                    64.0,
                    world.registryKey,
                    it
                )
            }
        }
    }
}