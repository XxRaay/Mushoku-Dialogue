package com.mushokuaddons.dialogue.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DialogueNode {
    private final String id;
    private final Component speakerName;
    private final Component text;
    private final List<DialogueChoice> choices;
    @Nullable
    private final Consumer<DialogueContext> onOpen;
    @Nullable
    private final ResourceLocation backgroundTexture;

    public DialogueNode(String id, Component speakerName, Component text, List<DialogueChoice> choices,
                        @Nullable Consumer<DialogueContext> onOpen, @Nullable ResourceLocation backgroundTexture) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.choices = choices;
        this.onOpen = onOpen;
        this.backgroundTexture = backgroundTexture;
    }

    public String id() {
        return id;
    }

    public Component speakerName() {
        return speakerName;
    }

    public Component text() {
        return text;
    }

    public List<DialogueChoice> choices() {
        return choices;
    }

    @Nullable
    public Consumer<DialogueContext> onOpen() {
        return onOpen;
    }

    @Nullable
    public ResourceLocation backgroundTexture() {
        return backgroundTexture;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private Component speakerName = Component.empty();
        private Component text = Component.empty();
        private final List<DialogueChoice> choices = new ArrayList<>();
        private Consumer<DialogueContext> onOpen = null;
        private ResourceLocation backgroundTexture = null;

        public Builder(String id) {
            this.id = id;
        }

        public Builder speaker(Component speakerName) {
            this.speakerName = speakerName;
            return this;
        }

        public Builder speaker(String speakerName) {
            this.speakerName = Component.literal(speakerName);
            return this;
        }

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public Builder text(String text) {
            this.text = Component.literal(text);
            return this;
        }

        public Builder choice(DialogueChoice choice) {
            this.choices.add(choice);
            return this;
        }

        public Builder choice(String choiceId, Consumer<DialogueChoice.Builder> choiceBuilderConsumer) {
            DialogueChoice.Builder b = DialogueChoice.builder(choiceId);
            choiceBuilderConsumer.accept(b);
            this.choices.add(b.build());
            return this;
        }

        public Builder onOpen(Consumer<DialogueContext> onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        public Builder background(ResourceLocation texture) {
            this.backgroundTexture = texture;
            return this;
        }

        public DialogueNode build() {
            return new DialogueNode(id, speakerName, text, List.copyOf(choices), onOpen, backgroundTexture);
        }
    }
}
