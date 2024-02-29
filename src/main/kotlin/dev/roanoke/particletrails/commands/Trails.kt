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
                        CommandManager.literal("give").requires { it.hasPermissionLevel(2) }
                            .then(
                                CommandManager.argument("player", StringArgumentType.string())
                                    .suggests { _, builder ->
                                        CommandSource.suggestMatching(Utils.getAllPlayerNames(), builder)
                                    }
                                    .then(
                                        CommandManager.argument("trail", StringArgumentType.string())
                                            .executes(giveTrail())
                                    )
                            )
                    )
                    .then(
                        CommandManager.literal("remove").requires { it.hasPermissionLevel(2) }
                            .executes(removeCommand())
                    )
            )
        })
    }

    private fun openGui(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val player = source.player!!
            GUIs.getTrailsGui(player).open()
            1
        }
    }

    private fun giveTrail(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val playerName = StringArgumentType.getString(ctx, "player")
            val trail = StringArgumentType.getString(ctx, "trail")

            val player = Utils.getPlayerByName(playerName)
            ParticleTrails.permissionManager.giveTrail(player!!, trail)
            player.sendMessage(Text.literal("§aYou have been given the §e$trail §aTrail"))

            source.sendFeedback({ Text.literal("§eGave §6$playerName $trail §eTrail") }, true)
            1
        }
    }

    private fun removeCommand(): Command<ServerCommandSource> {
        return Command { ctx: CommandContext<ServerCommandSource> ->
            val source = ctx.source

            val playerName = StringArgumentType.getString(ctx, "player")
            val trail = StringArgumentType.getString(ctx, "trail")

            val player = Utils.getPlayerByName(playerName)
            ParticleTrails.permissionManager.removeTrail(player!!, trail)

            source.sendFeedback({ Text.literal("§eRemoved trail §6$trail §efrom §6$playerName") }, true)
            1
        }
    }

}