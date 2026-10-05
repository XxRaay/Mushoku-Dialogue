package com.mushokuaddons.dialogue.api;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ChoiceAction {
    void execute(ServerPlayer player, DialogueContext context);

    static ChoiceAction goTo(String nextNodeId) {
        return (player, context) -> {
            DialogueNode nextNode = context.tree().nodes().get(nextNodeId);
            if (nextNode != null) {
                com.mushokuaddons.dialogue.manager.DialogueManager.transitionToNode(player, context, nextNode);
            } else {
                com.mushokuaddons.dialogue.manager.DialogueManager.closeDialogue(player);
            }
        };
    }

    static ChoiceAction run(BiConsumer<ServerPlayer, DialogueContext> action) {
        return action::accept;
    }

    static ChoiceAction run(Consumer<ServerPlayer> action) {
        return (player, ctx) -> action.accept(player);
    }

    static ChoiceAction close() {
        return (player, context) -> com.mushokuaddons.dialogue.manager.DialogueManager.closeDialogue(player);
    }

    static ChoiceAction sequence(ChoiceAction... actions) {
        return (player, context) -> {
            for (ChoiceAction action : actions) {
                action.execute(player, context);
            }
        };
    }
}
