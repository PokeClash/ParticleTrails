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

            if (size > 64)
                size = 64

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
            return ParticleTrails.trailPacks.find { it.name == name }
        }

        fun getTrailByName(name: String): Trail? {
            return ParticleTrails.trailPacks.flatMap { it.trails }.find { it.name == name }
        }

        fun getAllParticleTypes(): Map<ParticleType<*>, String> {
            return mapOf(
                ParticleTypes.AMBIENT_ENTITY_EFFECT to "ambient_entity_effect",
                ParticleTypes.ANGRY_VILLAGER to "angry_villager",
                ParticleTypes.ASH to "ash",
                ParticleTypes.BUBBLE to "bubble",
                ParticleTypes.BUBBLE_COLUMN_UP to "bubble_column_up",
                ParticleTypes.BUBBLE_POP to "bubble_pop",
                ParticleTypes.CAMPFIRE_COSY_SMOKE to "campfire_cosy_smoke",
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE to "campfire_signal_smoke",
                ParticleTypes.CHERRY_LEAVES to "cherry_leaves",
                ParticleTypes.CLOUD to "cloud",
                ParticleTypes.COMPOSTER to "composter",
                ParticleTypes.CRIMSON_SPORE to "crimson_spore",
                ParticleTypes.CRIT to "crit",
                ParticleTypes.CURRENT_DOWN to "current_down",
                ParticleTypes.DAMAGE_INDICATOR to "damage_indicator",
                ParticleTypes.DOLPHIN to "dolphin",
                ParticleTypes.DRAGON_BREATH to "dragon_breath",
                ParticleTypes.DRIPPING_DRIPSTONE_LAVA to "dripping_dripstone_lava",
                ParticleTypes.DRIPPING_DRIPSTONE_WATER to "dripping_dripstone_water",
                ParticleTypes.DRIPPING_HONEY to "dripping_honey",
                ParticleTypes.DRIPPING_LAVA to "dripping_lava",
                ParticleTypes.DRIPPING_OBSIDIAN_TEAR to "dripping_obsidian_tear",
                ParticleTypes.DRIPPING_WATER to "dripping_water",
                ParticleTypes.EFFECT to "effect",
                ParticleTypes.EGG_CRACK to "egg_crack",
                ParticleTypes.ELDER_GUARDIAN to "elder_guardian",
                ParticleTypes.ELECTRIC_SPARK to "electric_spark",
                ParticleTypes.ENCHANT to "enchant",
                ParticleTypes.ENCHANTED_HIT to "enchanted_hit",
                ParticleTypes.END_ROD to "end_rod",
                ParticleTypes.ENTITY_EFFECT to "entity_effect",
                ParticleTypes.EXPLOSION to "explosion",
                ParticleTypes.EXPLOSION_EMITTER to "explosion_emitter",
                ParticleTypes.FALLING_DRIPSTONE_LAVA to "falling_dripstone_lava",
                ParticleTypes.FALLING_DRIPSTONE_WATER to "falling_dripstone_water",
                ParticleTypes.FALLING_HONEY to "falling_honey",
                ParticleTypes.FALLING_LAVA to "falling_lava",
                ParticleTypes.FALLING_NECTAR to "falling_nectar",
                ParticleTypes.FALLING_OBSIDIAN_TEAR to "falling_obsidian_tear",
                ParticleTypes.FALLING_SPORE_BLOSSOM to "falling_spore_blossom",
                ParticleTypes.FALLING_WATER to "falling_water",
                ParticleTypes.FIREWORK to "firework",
                ParticleTypes.FISHING to "fishing",
                ParticleTypes.FLAME to "flame",
                ParticleTypes.FLASH to "flash",
                ParticleTypes.GLOW to "glow",
                ParticleTypes.GLOW_SQUID_INK to "glow_squid_ink",
                ParticleTypes.HAPPY_VILLAGER to "happy_villager",
                ParticleTypes.HEART to "heart",
                ParticleTypes.INSTANT_EFFECT to "instant_effect",
                ParticleTypes.LARGE_SMOKE to "large_smoke",
                ParticleTypes.LANDING_HONEY to "landing_honey",
                ParticleTypes.LANDING_LAVA to "landing_lava",
                ParticleTypes.LANDING_OBSIDIAN_TEAR to "landing_obsidian_tear",
                ParticleTypes.MYCELIUM to "mycelium",
                ParticleTypes.NAUTILUS to "nautilus",
                ParticleTypes.NOTE to "note",
                ParticleTypes.POOF to "poof",
                ParticleTypes.PORTAL to "portal",
                ParticleTypes.RAIN to "rain",
                ParticleTypes.REVERSE_PORTAL to "reverse_portal",
                ParticleTypes.SCRAPE to "scrape",
                ParticleTypes.SCULK_CHARGE to "sculk_charge",
                ParticleTypes.SCULK_CHARGE_POP to "sculk_charge_pop",
                ParticleTypes.SCULK_SOUL to "sculk_soul",
                ParticleTypes.SHRIEK to "shriek",
                ParticleTypes.SMOKE to "smoke",
                ParticleTypes.SMALL_FLAME to "small_flame",
                ParticleTypes.SNEEZE to "sneeze",
                ParticleTypes.SONIC_BOOM to "sonic_boom",
                ParticleTypes.SOUL to "soul",
                ParticleTypes.SOUL_FIRE_FLAME to "soul_fire_flame",
                ParticleTypes.SPLASH to "splash",
                ParticleTypes.SPORE_BLOSSOM_AIR to "spore_blossom_air",
                ParticleTypes.SQUID_INK to "squid_ink",
                ParticleTypes.SWEEP_ATTACK to "sweep_attack",
                ParticleTypes.TOTEM_OF_UNDYING to "totem_of_undying",
                ParticleTypes.UNDERWATER to "underwater",
                ParticleTypes.VIBRATION to "vibration",
                ParticleTypes.WARPED_SPORE to "warped_spore",
                ParticleTypes.WAX_OFF to "wax_off",
                ParticleTypes.WAX_ON to "wax_on",
                ParticleTypes.WHITE_ASH to "white_ash",
                ParticleTypes.WITCH to "witch",
                ParticleTypes.SNOWFLAKE to "snowflake"
            )
        }


        fun getParticleFromString(particle: String): ParticleType<*> {
            getAllParticleTypes().forEach {
                if (it.value.lowercase() == particle.lowercase())
                    return it.key
            }
            return ParticleTypes.AMBIENT_ENTITY_EFFECT
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
                    val radius = 1.2
                    val rotationSpeed = 0.1
                    val maxRandomYOffset = 0.2
                    val random = java.util.Random()

                    val worldTick = player.world.time
                    val angle = worldTick * rotationSpeed
                    val randomYOffset = (random.nextDouble() * 2 - 1) * maxRandomYOffset * cos(angle)

                    val x = player.x + radius * cos(angle)
                    val y = player.eyeY + randomYOffset
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
                    val radius = 1
                    val rotationSpeed = 0.05

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