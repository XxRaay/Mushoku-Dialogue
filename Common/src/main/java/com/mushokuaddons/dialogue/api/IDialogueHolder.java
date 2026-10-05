package com.mushokuaddons.dialogue.api;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface IDialogueHolder {
    @Nullable
    DialogueTree getDialogueTree(ServerPlayer player);
}
