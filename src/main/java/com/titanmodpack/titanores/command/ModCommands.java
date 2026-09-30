package com.titanmodpack.titanores.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.event.PlayerHealthEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Collections;

// /titanores hearts get [player]
// /titanores hearts reset [players]
// /titanores hearts set <count> [players]
// Requires permission level 2 (operators / cheats enabled).
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(TitanOres.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("hearts")
                        .then(Commands.literal("get")
                                .executes(ctx -> get(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> get(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("reset")
                                .executes(ctx -> set(ctx.getSource(), self(ctx.getSource()), 0))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(ctx -> set(ctx.getSource(), EntityArgument.getPlayers(ctx, "players"), 0))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("count", IntegerArgumentType.integer(0, 255))
                                        .executes(ctx -> set(ctx.getSource(), self(ctx.getSource()), IntegerArgumentType.getInteger(ctx, "count")))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .executes(ctx -> set(ctx.getSource(), EntityArgument.getPlayers(ctx, "players"),
                                                        IntegerArgumentType.getInteger(ctx, "count"))))))));
    }

    private static Collection<ServerPlayerEntity> self(CommandSource source) throws CommandSyntaxException {
        return Collections.singletonList(source.getPlayerOrException());
    }

    private static int get(CommandSource source, ServerPlayerEntity player) {
        int count = PlayerHealthEvents.getHeartItems(player);
        source.sendSuccess(new TranslationTextComponent("commands.titanores.hearts.get", player.getDisplayName(), count, count * 2), false);
        return count;
    }

    private static int set(CommandSource source, Collection<ServerPlayerEntity> players, int count) {
        for (ServerPlayerEntity player : players) {
            PlayerHealthEvents.setHeartItems(player, count);
            source.sendSuccess(new TranslationTextComponent("commands.titanores.hearts.set", player.getDisplayName(), count, count * 2), true);
        }
        return players.size();
    }
}
