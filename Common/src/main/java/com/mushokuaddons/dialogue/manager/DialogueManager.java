package com.mushokuaddons.dialogue.manager;

import com.mushokuaddons.dialogue.api.DialogueChoice;
import com.mushokuaddons.dialogue.api.DialogueContext;
import com.mushokuaddons.dialogue.api.DialogueNode;
import com.mushokuaddons.dialogue.api.DialogueRegistry;
import com.mushokuaddons.dialogue.api.DialogueTree;
import com.mushokuaddons.dialogue.network.CloseDialoguePacket;
import com.mushokuaddons.dialogue.network.OpenDialoguePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DialogueManager {
    public record ActiveDialogueSession(
            DialogueTree tree,
            DialogueNode currentNode,
            @Nullable Entity targetEntity,
            DialogueContext context,
            List<DialogueChoice> availableChoices
    ) {}

    private static final Map<UUID, ActiveDialogueSession> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    public static void openDialogue(ServerPlayer player, String treeId, @Nullable Entity entity) {
        DialogueTree tree = DialogueRegistry.get(treeId);
        if (tree != null) {
            openDialogue(player, tree, entity);
        }
    }

    public static void openDialogue(ServerPlayer player, DialogueTree tree, @Nullable Entity entity) {
        DialogueNode startNode = tree.getStartNode();
        if (startNode == null) return;

        DialogueContext context = new DialogueContext(player, entity, tree, startNode);
        sendNode(player, tree, startNode, entity, context);
    }

    public static void transitionToNode(ServerPlayer player, DialogueContext context, DialogueNode nextNode) {
        context.setCurrentNode(nextNode);
        sendNode(player, context.tree(), nextNode, context.entity(), context);
    }

    private static void sendNode(ServerPlayer player, DialogueTree tree, DialogueNode node, @Nullable Entity entity, DialogueContext context) {
        if (node.onOpen() != null) {
            node.onOpen().accept(context);
        }

        List<DialogueChoice> validChoices = new ArrayList<>();
        List<OpenDialoguePacket.ClientChoiceEntry> clientEntries = new ArrayList<>();

        int clientIndex = 0;
        for (DialogueChoice choice : node.choices()) {
            boolean test = choice.test(player);
            if (!test && !choice.isVisibleWhenDisabled()) {
                continue;
            }

            validChoices.add(choice);
            Component tooltip = choice.disabledTooltip() != null ? choice.disabledTooltip() : Component.empty();
            clientEntries.add(new OpenDialoguePacket.ClientChoiceEntry(clientIndex++, choice.text(), test, tooltip));
        }

        ActiveDialogueSession session = new ActiveDialogueSession(tree, node, entity, context, validChoices);
        ACTIVE_SESSIONS.put(player.getUUID(), session);

        int entityId = entity != null ? entity.getId() : -1;
        OpenDialoguePacket packet = new OpenDialoguePacket(
                node.speakerName(),
                node.text(),
                entityId,
                clientEntries,
                node.hasInput(),
                node.inputPlaceholder(),
                node.initialInput(),
                node.maxInputLength()
        );
        NetworkManager.sendToPlayer(player, packet);
    }

    public static void handleChoice(ServerPlayer player, int choiceIndex) {
        handleChoice(player, choiceIndex, "");
    }

    public static void handleChoice(ServerPlayer player, int choiceIndex, String input) {
        ActiveDialogueSession session = ACTIVE_SESSIONS.get(player.getUUID());
        if (session == null) return;

        if (session.targetEntity() != null && !session.targetEntity().isAlive()) {
            closeDialogue(player);
            return;
        }

        if (session.targetEntity() != null && player.distanceToSqr(session.targetEntity()) > 64.0) {
            closeDialogue(player);
            return;
        }

        if (choiceIndex < 0 || choiceIndex >= session.availableChoices().size()) {
            return;
        }

        DialogueChoice chosen = session.availableChoices().get(choiceIndex);
        if (!chosen.test(player)) {
            return;
        }

        session.context().setInput(input != null ? input : "");
        chosen.action().execute(player, session.context());
    }

    public static void closeDialogue(ServerPlayer player) {
        ACTIVE_SESSIONS.remove(player.getUUID());
        NetworkManager.sendToPlayer(player, new CloseDialoguePacket());
    }

    public static void handleClientClosed(ServerPlayer player) {
        ACTIVE_SESSIONS.remove(player.getUUID());
    }

    public static void onPlayerDisconnect(UUID playerId) {
        ACTIVE_SESSIONS.remove(playerId);
    }
}
