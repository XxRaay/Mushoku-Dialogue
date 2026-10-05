package com.mushokuaddons.dialogue.manager;

import com.mushokuaddons.dialogue.api.DialogueTree;
import com.mushokuaddons.dialogue.api.IDialogueHolder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class DialogueInteractionHandler {
    private static final Map<Class<? extends Entity>, BiFunction<ServerPlayer, Entity, DialogueTree>> ENTITY_DIALOGUE_PROVIDERS = new ConcurrentHashMap<>();

    public static <T extends Entity> void registerEntityDialogue(Class<T> entityClass, BiFunction<ServerPlayer, T, DialogueTree> provider) {
        ENTITY_DIALOGUE_PROVIDERS.put(entityClass, (player, entity) -> provider.apply(player, entityClass.cast(entity)));
    }

    public static void register() {
        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer serverPlayer)) {
                return EventResult.pass();
            }

            if (entity instanceof IDialogueHolder holder) {
                DialogueTree tree = holder.getDialogueTree(serverPlayer);
                if (tree != null) {
                    DialogueManager.openDialogue(serverPlayer, tree, entity);
                    return EventResult.interruptTrue();
                }
            }

            for (Map.Entry<Class<? extends Entity>, BiFunction<ServerPlayer, Entity, DialogueTree>> entry : ENTITY_DIALOGUE_PROVIDERS.entrySet()) {
                if (entry.getKey().isInstance(entity)) {
                    DialogueTree tree = entry.getValue().apply(serverPlayer, entity);
                    if (tree != null) {
                        DialogueManager.openDialogue(serverPlayer, tree, entity);
                        return EventResult.interruptTrue();
                    }
                }
            }

            return EventResult.pass();
        });

        PlayerEvent.PLAYER_QUIT.register(player -> {
            DialogueManager.onPlayerDisconnect(player.getUUID());
        });
    }
}
