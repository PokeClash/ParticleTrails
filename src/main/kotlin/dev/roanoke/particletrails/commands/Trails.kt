package dev.roanoke.particletrails.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.roanoke.particletrails.ParticleTrails
import dev.roanoke.particletrails.gui.GUIs
import dev.roanoke.particletrails.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class Trails(private val main: ParticleTrails) {

    init {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("trails").requires { Permissions.check(it, "particletrails.use", 2) }
                    .executes(openGui())
                    .then(
                        CommandManager.literal("reload").requires { Permissions.check(it, "particletrails.reload", 2) }
                            .executes { ctx ->
                                val source = ctx.source
                                ParticleTrails.config.loadPacks()
                                source.sendFeedback({ Text.literal("§eReloaded trails") }, true)
                                1
                            }
                    )
                    .then(
                        CommandManager.literal("give").requires { Permissions.check(it, "particletrails.give", 2) }
                            .then(
                                CommandManager.argument("player", StringArgumentType.string())
                                    .suggests { _, builder ->
                                        CommandSource.suggestMatching(Utils.getAllPlayerNames(), builder)
                                    }
                                    .then(
                                        CommandManager.literal("pack")
                                            .then(
                                                CommandManager.argument("pack", StringArgumentType.string())
                                                    .suggests { _, builder ->
                                                        CommandSource.suggestMatching(ParticleTrails.trailPacks.map { it.name }.toSet(), builder)
                                                    }
                                                    .executes(givePack())
                                            )
                                    )
                                    .then(
                                        CommandManager.literal("trail")
                                            .then(
                                                CommandManager.argument("trail", StringArgumentType.string())
                                                    .suggests { _, builder ->
                                                        CommandSource.suggestMatching(ParticleTrails.trailPacks.flatMap { it.trails }.map { it.name }.toSet(), builder)
                                                    }
                                                    .executes(giveTrail())
                                            )
                                    )
                            )
                    )
                    .then(
                        CommandManager.literal("remove").requires { Permissions.check(it, "particletrails.remove", 2) }
                            .then(
                                CommandManager.argument("player", StringArgumentType.string())
                                    .suggests { _, builder ->
                                        CommandSource.suggestMatching(Utils.getAllPlayerNames(), builder)
                                    }
                                    .then(
                                        CommandManager.argument("trail", StringArgumentType.string())
                                            .suggests { _, builder ->
                                                CommandSource.suggestMatching(ParticleTrails.trailPacks.flatMap { it.trails }.map { it.name }.toSet(), builder)
                                            }
                                            .executes(removeTrail())
                                    )
                            )
                    )
            )
        })
    }

    private fun openGui(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val player = source.player!!
            GUIs.getCategoriesGui(player).open()
            1
        }
    }

    private fun giveTrail(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val playerName = StringArgumentType.getString(ctx, "player")
            val trailName = StringArgumentType.getString(ctx, "trail")

            val player = Utils.getPlayerByName(playerName)
            val trail = Utils.getTrailByName(trailName)
            if (trail == null) {
                source.sendFeedback({ Text.literal("§cThat trail does not exist") }, true)
                return@Command 0
            }

            Utils.giveTrail(player, trail)

            player?.sendMessage(Text.literal("§aYou have been given the ").append(trail.getDisplayName()).append(Text.literal(" §aTrail")))

            source.sendFeedback({ Text.literal("§eGave §6$playerName ").append(trail.getDisplayName()).append(" §eTrail") }, true)
            1
        }
    }

    private fun givePack(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val playerName = StringArgumentType.getString(ctx, "player")
            val packName = StringArgumentType.getString(ctx, "pack")

            val player = Utils.getPlayerByName(playerName)
            val pack = Utils.getPackByName(packName)
            if (pack == null) {
                source.sendFeedback({ Text.literal("§cThat pack does not exist") }, true)
                return@Command 0
            }

            Utils.givePack(player, pack)

            player?.sendMessage(Text.literal("§aYou have been given the ").append(pack.getDisplayName()).append(Text.literal(" §aPack")))

            source.sendFeedback({ Text.literal("§eGave §6$playerName ").append(pack.getDisplayName()).append(" §ePack") }, true)
            1
        }
    }

    private fun removeTrail(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val playerName = StringArgumentType.getString(ctx, "player")
            val trailName = StringArgumentType.getString(ctx, "trail")

            val player = Utils.getPlayerByName(playerName)
            val trail = Utils.getTrailByName(trailName)
            if (trail == null) {
                source.sendFeedback({ Text.literal("§cThat trail does not exist") }, true)
                return@Command 0
            }

            Utils.removeTrail(player, trail)

            source.sendFeedback({ Text.literal("§eRemoved trail ").append(trail.getDisplayName()).append(Text.literal("§efrom §6$playerName")) }, true)
            1
        }
    }

}