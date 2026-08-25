package com.mars.stairstoblocks.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mars.stairstoblocks.StairsToBlocksConfig.show_wishful_recipes_message;


@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void placeNewPlayer(Connection $$0, ServerPlayer player, CallbackInfo ci) {
        if (!show_wishful_recipes_message) return;

        Component msg = Component.literal("")
                .append(
                        Component.literal("Craft Stairs Back Into Blocks")
                                .withStyle(ChatFormatting.GREEN)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/craft-stairs-back-into-blocks"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("The mod you are currently running")))
                                )
                )
                .append(
                        Component.literal(" is being deprecated and replaced by ")
                )
                .append(
                        Component.literal("Wishful Recipes")
                                .withStyle(ChatFormatting.GREEN)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/wishful-recipes"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("The mod you should definitely switch to :)")))
                                )
                )
                .append(
                        Component.literal(". Wishful Recipes unifies all of my crafting mods into a single one, " +
                                        "and it dynamically adds recipes without you having to add each new recipe into its config. " +
                                        "Please consider checking it out and switching to it here: ")
                )
                .append(
                        Component.literal("[CurseForge]")
                                .withStyle(style -> style
                                        .withColor(0xFF6A00)
                                        .withUnderlined(true)
                                        .withBold(true)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/wishful-recipes"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("https://www.curseforge.com/minecraft/mc-mods/wishful-recipes")))
                                )
                )
                .append(
                        Component.literal(" ")
                )
                .append(
                        Component.literal("[Modrinth]")
                                .withStyle(style -> style
                                        .withColor(0x119F36)
                                        .withUnderlined(true)
                                        .withBold(true)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/wishful-recipes"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("https://modrinth.com/mod/wishful-recipes")))
                                )
                )
                .append(
                        Component.literal(" (You can disable this message in this mod's config)")
                                .withStyle(ChatFormatting.GRAY)
                );

        player.sendSystemMessage(msg);
    }
}
