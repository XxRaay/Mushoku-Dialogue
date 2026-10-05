package com.mushokuaddons.dialogue.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DialogueChoice {
    private final String id;
    private final Component text;
    private final ChoiceAction action;
    private final Predicate<ServerPlayer> condition;
    private final boolean visibleWhenDisabled;
    @Nullable
    private final Component disabledTooltip;

    public DialogueChoice(String id, Component text, ChoiceAction action, Predicate<ServerPlayer> condition, boolean visibleWhenDisabled, @Nullable Component disabledTooltip) {
        this.id = id;
        this.text = text;
        this.action = action;
        this.condition = condition;
        this.visibleWhenDisabled = visibleWhenDisabled;
        this.disabledTooltip = disabledTooltip;
    }

    public String id() {
        return id;
    }

    public Component text() {
        return text;
    }

    public ChoiceAction action() {
        return action;
    }

    public boolean test(ServerPlayer player) {
        return condition == null || condition.test(player);
    }

    public boolean isVisibleWhenDisabled() {
        return visibleWhenDisabled;
    }

    @Nullable
    public Component disabledTooltip() {
        return disabledTooltip;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private Component text;
        private ChoiceAction action = ChoiceAction.close();
        private Predicate<ServerPlayer> condition = player -> true;
        private boolean visibleWhenDisabled = false;
        private Component disabledTooltip = null;

        public Builder(String id) {
            this.id = id;
        }

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public Builder text(String plainText) {
            this.text = Component.literal(plainText);
            return this;
        }

        public Builder action(ChoiceAction action) {
            this.action = action;
            return this;
        }

        public Builder condition(Predicate<ServerPlayer> condition) {
            this.condition = condition;
            return this;
        }

        public Builder visibleWhenDisabled(boolean visible) {
            this.visibleWhenDisabled = visible;
            return this;
        }

        public Builder disabledTooltip(Component tooltip) {
            this.disabledTooltip = tooltip;
            return this;
        }

        public DialogueChoice build() {
            if (text == null) {
                text = Component.literal(id);
            }
            return new DialogueChoice(id, text, action, condition, visibleWhenDisabled, disabledTooltip);
        }
    }
}
