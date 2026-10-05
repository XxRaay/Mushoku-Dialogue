package com.mushokuaddons.dialogue.api;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogueRegistry {
    private static final Map<String, DialogueTree> TREES = new ConcurrentHashMap<>();

    public static void register(DialogueTree tree) {
        TREES.put(tree.id(), tree);
    }

    @Nullable
    public static DialogueTree get(String id) {
        return TREES.get(id);
    }

    public static boolean contains(String id) {
        return TREES.containsKey(id);
    }

    public static void clear() {
        TREES.clear();
    }
}
