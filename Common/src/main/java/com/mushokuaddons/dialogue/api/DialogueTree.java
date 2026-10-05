package com.mushokuaddons.dialogue.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DialogueTree {
    private final String id;
    private final String startNodeId;
    private final Map<String, DialogueNode> nodes;

    public DialogueTree(String id, String startNodeId, Map<String, DialogueNode> nodes) {
        this.id = id;
        this.startNodeId = startNodeId;
        this.nodes = Collections.unmodifiableMap(new HashMap<>(nodes));
    }

    public String id() {
        return id;
    }

    public String startNodeId() {
        return startNodeId;
    }

    public Map<String, DialogueNode> nodes() {
        return nodes;
    }

    public DialogueNode getStartNode() {
        return nodes.get(startNodeId);
    }

    public DialogueNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private String startNodeId = "start";
        private final Map<String, DialogueNode> nodes = new HashMap<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder startNode(String startNodeId) {
            this.startNodeId = startNodeId;
            return this;
        }

        public Builder node(DialogueNode node) {
            this.nodes.put(node.id(), node);
            return this;
        }

        public Builder node(String nodeId, Consumer<DialogueNode.Builder> nodeBuilderConsumer) {
            DialogueNode.Builder builder = DialogueNode.builder(nodeId);
            nodeBuilderConsumer.accept(builder);
            this.nodes.put(nodeId, builder.build());
            return this;
        }

        public DialogueTree build() {
            if (!nodes.containsKey(startNodeId) && !nodes.isEmpty()) {
                startNodeId = nodes.keySet().iterator().next();
            }
            return new DialogueTree(id, startNodeId, nodes);
        }
    }
}
