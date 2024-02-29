package dev.roanoke.particletrails.gui

import dev.roanoke.particletrails.ParticleTrails
import dev.roanoke.particletrails.utils.TrailCategory
import dev.roanoke.particletrails.utils.Utils
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class GUIs {
    companion object {
        private fun fillGUI(gui: SimpleGui) {
            var freeSlot = gui.firstEmptySlot
            while (freeSlot != -1) {
                gui.setSlot(
                    freeSlot,
                    GuiElementBuilder.from(Items.BLACK_STAINED_GLASS_PANE.defaultStack.setCustomName(Text.literal("")))
                )
                freeSlot = gui.firstEmptySlot
            }
        }

        fun getTrailsGui(player: ServerPlayerEntity, category: TrailCategory): SimpleGui {
            val gui = SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false)
            gui.title = Text.literal("§5§lParticle Trails")

            val trails = ParticleTrails.trails.mapNotNull {
                if (ParticleTrails.permissionManager.canUseTrail(player, it.name) || player.hasPermissionLevel(2)) {
                    GuiElementBuilder.from(Utils.createItem(it))
                        .setCallback { _, _, _ ->
                            Utils.applyTrail(player, it)
                            player.sendMessage(Text.literal("§eYou have selected ").append(it.getDisplayName()).append(Text.literal("§e as your trail.")))
                            gui.close()
                        }
                } else {
                    null
                }
            }

            val paginatedSection: PaginatedSection = if (trails.size > 21) {
                PaginatedSection(trails).setSlotRanges(
                    listOf(
                        SlotRange(10, 16), SlotRange(19, 25), SlotRange(29, 33)
                    )
                )
            } else {
                PaginatedSection(trails).setSlotRanges(
                    listOf(
                        SlotRange(10, 16), SlotRange(19, 25), SlotRange(28, 34)
                    )
                )
            }

            paginatedSection.applyToGui(gui)

            if (trails.size > 21) {
                gui.setSlot(28,
                    GuiElementBuilder(Items.PLAYER_HEAD).setSkullOwner(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=",
                        null,
                        null
                    )
                        .setName(Text.literal("§fPrevious"))
                        .setCallback { _, _, _ ->
                            paginatedSection.decrementPage()
                            paginatedSection.applyToGui(gui)
                        })

                gui.setSlot(34,
                    GuiElementBuilder(Items.PLAYER_HEAD).setSkullOwner(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19",
                        null,
                        null
                    )
                        .setName(Text.literal("§fNext"))
                        .setCallback { _, _, _ ->
                            paginatedSection.incrementPage()
                            paginatedSection.applyToGui(gui)
                        })
            }

            gui.setSlot(40,
                GuiElementBuilder(Items.RED_STAINED_GLASS_PANE)
                    .setName(Text.literal("§4ClearTrail"))
                    .setCallback { _, _, _ ->
                        Utils.clearTrail(player)
                        player.sendMessage(Text.literal("§eCleared trail."))
                        gui.close()
                    }
                )

            val currentTrail = ParticleTrails.playerTrail[player.uuid]

            gui.setSlot(
                4, GuiElementBuilder(Items.PLAYER_HEAD).setSkullOwner(player.gameProfile, player.server)
                    .setName(
                        if (currentTrail == null) {
                            Text.literal("§eCurrent Trail: §cNone")
                        } else {
                            Text.literal("§eCurrent Trail: ").append(currentTrail.getDisplayName())
                        }
                    )
                    .build()
            )

            fillGUI(gui)

            return gui

        }

    }
}