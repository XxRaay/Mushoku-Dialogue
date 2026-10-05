package com.mushokuaddons.dialogue.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DialogueContext {
    private final ServerPlayer player;
    @Nullable
    private final Entity entity;
    private final DialogueTree tree;
    private DialogueNode currentNode;
    private final Map<String, Object> data = new HashMap<>();

    public DialogueContext(ServerPlayer player, @Nullable Entity entity, DialogueTree tree, DialogueNode currentNode) {
        this.player = player;
        this.entity = entity;
        this.tree = tree;
        this.currentNode = currentNode;
    }

    public ServerPlayer player() {
        return player;
    }

    @Nullable
    public Entity entity() {
        return entity;
    }

    public DialogueTree tree() {
        return tree;
    }

    public DialogueNode currentNode() {
        return currentNode;
    }

    public void setCurrentNode(DialogueNode currentNode) {
        this.currentNode = currentNode;
    }

    private String input = "";

    public String input() {
        return input != null ? input : "";
    }

    public void setInput(String input) {
        this.input = input != null ? input : "";
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(String key) {
        return (T) data.get(key);
    }
}
