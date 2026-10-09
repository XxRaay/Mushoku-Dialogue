package com.mushokuaddons.dialogue.client.gui;

import com.mushokuaddons.dialogue.network.CloseDialogueC2SPacket;
import com.mushokuaddons.dialogue.network.OpenDialoguePacket;
import com.mushokuaddons.dialogue.network.SelectChoicePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.components.EditBox;
import java.util.List;

public class DialogueScreen extends Screen {
    private final Component speaker;
    private final Component fullText;
    private final int entityId;
    private final List<OpenDialoguePacket.ClientChoiceEntry> choices;
    private final boolean hasInput;
    private final Component inputPlaceholder;
    private final String initialInput;
    private final int maxInputLength;

    private EditBox inputBox;

    private int revealedChars = 0;
    private int tickTimer = 0;
    private boolean typingComplete = false;

    private int boxWidth = 460;
    private int boxHeight = 140;
    private int boxX = 0;
    private int boxY = 0;

    private int hoveredChoice = -1;
    private int scrollOffset = 0;
    private int visibleChoicesCount = 0;
    private boolean isDraggingScroll = false;

    public DialogueScreen(Component speaker, Component text, int entityId, List<OpenDialoguePacket.ClientChoiceEntry> choices,
                          boolean hasInput, Component inputPlaceholder, String initialInput, int maxInputLength) {
        super(Component.translatable("gui.mushokudialogue.title"));
        this.speaker = speaker;
        this.fullText = text;
        this.entityId = entityId;
        this.choices = choices;
        this.hasInput = hasInput;
        this.inputPlaceholder = inputPlaceholder != null ? inputPlaceholder : Component.empty();
        this.initialInput = initialInput != null ? initialInput : "";
        this.maxInputLength = maxInputLength > 0 ? maxInputLength : 32;
    }

    public DialogueScreen(Component speaker, Component text, int entityId, List<OpenDialoguePacket.ClientChoiceEntry> choices) {
        this(speaker, text, entityId, choices, false, Component.empty(), "", 32);
    }

    @Override
    protected void init() {
        super.init();
        boxWidth = Math.min(480, this.width - 32);
        List<FormattedCharSequence> wrapped = this.font.split(this.fullText, boxWidth - 36);
        int textHeight = Math.max(wrapped.size() * 12, 24);
        int inputAreaHeight = hasInput ? 32 : 0;

        int maxPossibleChoices = Math.max(2, Math.min(5, (this.height - 110 - textHeight - inputAreaHeight) / 22));
        visibleChoicesCount = Math.min(choices.size(), Math.max(1, maxPossibleChoices));
        int choicesHeight = choices.isEmpty() ? 0 : (visibleChoicesCount * 22 + 10);

        boxHeight = 24 + textHeight + inputAreaHeight + choicesHeight + 8;
        boxX = (this.width - boxWidth) / 2;
        boxY = this.height - boxHeight - 16;

        int maxScroll = Math.max(0, choices.size() - visibleChoicesCount);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        if (hasInput) {
            int inputY = boxY + 24 + textHeight + 4;
            this.inputBox = new EditBox(this.font, boxX + 18, inputY, boxWidth - 36, 20, inputPlaceholder);
            this.inputBox.setMaxLength(maxInputLength);
            this.inputBox.setValue(initialInput);
            this.inputBox.setHint(inputPlaceholder);
            this.inputBox.setTextColor(0xFFFFFFFF);
            this.addRenderableWidget(this.inputBox);
            this.setInitialFocus(this.inputBox);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Disabled: do not blur or tint the 3D world when talking to NPCs
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        // Disabled: completely prevents Minecraft 1.21 menu blur shader ("зрение -5")
    }

    @Override
    public void tick() {
        super.tick();
        if (!typingComplete) {
            tickTimer++;
            String fullStr = fullText.getString();
            revealedChars = Math.min(revealedChars + 2, fullStr.length());
            if (revealedChars >= fullStr.length()) {
                typingComplete = true;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Soft bottom cinematic gradient only behind dialogue area (keeps the NPC & upper world crystal clear)
        graphics.fillGradient(0, Math.max(0, boxY - 36), this.width, this.height, 0x00000000, 0x55000000);

        // Dialogue Box outer shadow
        graphics.fill(boxX - 3, boxY - 3, boxX + boxWidth + 3, boxY + boxHeight + 3, 0x55000000);
        // Dialogue Box Main Background
        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xF2141210);

        // Ornate Golden/Bronze Borders
        graphics.renderOutline(boxX, boxY, boxWidth, boxHeight, 0xFF8B6B38);
        graphics.renderOutline(boxX + 2, boxY + 2, boxWidth - 4, boxHeight - 4, 0xFF4A3A22);

        // Corner Ornaments
        renderCornerDecorations(graphics, boxX, boxY, boxWidth, boxHeight);

        // Speaker Name Plate (if present)
        String speakerStr = speaker.getString().trim();
        if (!speakerStr.isEmpty()) {
            int nameWidth = this.font.width(speaker) + 28;
            int nameX = boxX + 16;
            int nameY = boxY - 14;

            graphics.fill(nameX, nameY, nameX + nameWidth, nameY + 18, 0xFF1C1814);
            graphics.renderOutline(nameX, nameY, nameWidth, 18, 0xFFD4AF37);
            graphics.drawString(this.font, "❖ " + speakerStr + " ❖", nameX + 8, nameY + 5, 0xFFFFE898, true);
        }

        // Dialogue Text Area
        String textToDisplay = typingComplete ? fullText.getString() : fullText.getString().substring(0, Math.min(revealedChars, fullText.getString().length()));
        List<FormattedCharSequence> wrappedLines = this.font.split(Component.literal(textToDisplay), boxWidth - 36);

        int textY = boxY + 16;
        for (FormattedCharSequence line : wrappedLines) {
            graphics.drawString(this.font, line, boxX + 18, textY, 0xFFFFFFFF, true);
            textY += 12;
        }

        if (hasInput && inputBox != null) {
            inputBox.render(graphics, mouseX, mouseY, partialTick);
            if (inputBox.isFocused()) {
                graphics.renderOutline(inputBox.getX() - 1, inputBox.getY() - 1, inputBox.getWidth() + 2, inputBox.getHeight() + 2, 0xFFFFD700);
            }
        }

        // Choices
        hoveredChoice = -1;
        Component tooltipToRender = null;

        if (!choices.isEmpty()) {
            int choicesStartY = boxY + boxHeight - (visibleChoicesCount * 22) - 8;
            graphics.fill(boxX + 16, choicesStartY - 6, boxX + boxWidth - 16, choicesStartY - 5, 0x448B6B38);

            boolean hasScrollBar = choices.size() > visibleChoicesCount;
            int scrollbarWidth = 6;
            int choiceWidth = hasScrollBar ? (boxWidth - 32 - scrollbarWidth - 6) : (boxWidth - 32);
            int choiceX = boxX + 16;

            for (int vi = 0; vi < visibleChoicesCount; vi++) {
                int i = vi + scrollOffset;
                if (i >= choices.size()) break;

                OpenDialoguePacket.ClientChoiceEntry entry = choices.get(i);
                int choiceY = choicesStartY + (vi * 22);
                int choiceHeight = 20;

                boolean isHovered = mouseX >= choiceX && mouseX <= choiceX + choiceWidth &&
                                    mouseY >= choiceY && mouseY <= choiceY + choiceHeight;

                if (isHovered) {
                    hoveredChoice = i;
                    if (!entry.enabled() && !entry.disabledTooltip().getString().isEmpty()) {
                        tooltipToRender = entry.disabledTooltip();
                    }
                }

                int bgCol = entry.enabled() ? (isHovered ? 0xFF2A241C : 0xFF1B1814) : 0xFF12100E;
                int borderCol = entry.enabled() ? (isHovered ? 0xFFFFD700 : 0xFF5C4729) : 0xFF332A20;

                graphics.fill(choiceX, choiceY, choiceX + choiceWidth, choiceY + choiceHeight, bgCol);
                graphics.renderOutline(choiceX, choiceY, choiceWidth, choiceHeight, borderCol);

                // Choice index & text
                String numPrefix = "[" + (i + 1) + "] ";
                String prefix = isHovered && entry.enabled() ? "◆ " : "  ";
                int textCol = entry.enabled() ? (isHovered ? 0xFFFFFFFF : 0xFFE0D8CB) : 0xFF8A8276;

                String fullChoiceStr = prefix + numPrefix + entry.text().getString();
                if (!entry.enabled()) {
                    fullChoiceStr += " 🔒";
                }

                graphics.drawString(this.font, fullChoiceStr, choiceX + 8, choiceY + 6, textCol, true);
            }

            // Scrollbar rendering
            if (hasScrollBar) {
                int scrollTrackX = boxX + boxWidth - 16 - scrollbarWidth;
                int scrollTrackY = choicesStartY;
                int scrollTrackHeight = visibleChoicesCount * 22 - 2;

                // Track background
                graphics.fill(scrollTrackX, scrollTrackY, scrollTrackX + scrollbarWidth, scrollTrackY + scrollTrackHeight, 0xFF141210);
                graphics.renderOutline(scrollTrackX, scrollTrackY, scrollbarWidth, scrollTrackHeight, 0xFF4A3A22);

                // Thumb
                int maxScroll = choices.size() - visibleChoicesCount;
                int thumbH = Math.max(10, (scrollTrackHeight * visibleChoicesCount) / choices.size());
                int thumbY = scrollTrackY + (int) ((float) scrollOffset / maxScroll * (scrollTrackHeight - thumbH));

                boolean isThumbHovered = mouseX >= scrollTrackX - 2 && mouseX <= scrollTrackX + scrollbarWidth + 2 &&
                                         mouseY >= thumbY && mouseY <= thumbY + thumbH;
                int thumbColor = (isDraggingScroll || isThumbHovered) ? 0xFFFFD700 : 0xFF8B6B38;
                int thumbBorder = (isDraggingScroll || isThumbHovered) ? 0xFFFFE898 : 0xFF5C4729;

                graphics.fill(scrollTrackX + 1, thumbY + 1, scrollTrackX + scrollbarWidth - 1, thumbY + thumbH - 1, thumbColor);
                graphics.renderOutline(scrollTrackX, thumbY, scrollbarWidth, thumbH, thumbBorder);

                if (scrollOffset > 0) {
                    graphics.drawString(this.font, "▲", scrollTrackX - 1, scrollTrackY - 9, 0xFFD4AF37, false);
                }
                if (scrollOffset < maxScroll) {
                    graphics.drawString(this.font, "▼", scrollTrackX - 1, scrollTrackY + scrollTrackHeight + 2, 0xFFD4AF37, false);
                }
            }
        }

        if (tooltipToRender != null) {
            graphics.renderTooltip(this.font, tooltipToRender, mouseX, mouseY);
        }
    }

    private void renderCornerDecorations(GuiGraphics graphics, int x, int y, int w, int h) {
        int gold = 0xFFD4AF37;
        // Top Left
        graphics.fill(x - 1, y - 1, x + 5, y + 1, gold);
        graphics.fill(x - 1, y - 1, x + 1, y + 5, gold);

        // Top Right
        graphics.fill(x + w - 5, y - 1, x + w + 1, y + 1, gold);
        graphics.fill(x + w - 1, y - 1, x + w + 1, y + 5, gold);

        // Bottom Left
        graphics.fill(x - 1, y + h - 1, x + 5, y + h + 1, gold);
        graphics.fill(x - 1, y + h - 5, x + 1, y + h + 1, gold);

        // Bottom Right
        graphics.fill(x + w - 5, y + h - 1, x + w + 1, y + h + 1, gold);
        graphics.fill(x + w - 1, y + h - 5, x + w + 1, y + h + 1, gold);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (!typingComplete) {
                typingComplete = true;
                revealedChars = fullText.getString().length();
                playClickSound(1.2f);
                return true;
            }

            // Scrollbar track / thumb click
            if (choices.size() > visibleChoicesCount) {
                int scrollbarWidth = 6;
                int scrollTrackX = boxX + boxWidth - 16 - scrollbarWidth;
                int choicesStartY = boxY + boxHeight - (visibleChoicesCount * 22) - 8;
                int scrollTrackHeight = visibleChoicesCount * 22 - 2;

                if (mouseX >= scrollTrackX - 3 && mouseX <= scrollTrackX + scrollbarWidth + 3 &&
                    mouseY >= choicesStartY && mouseY <= choicesStartY + scrollTrackHeight) {
                    isDraggingScroll = true;
                    updateScrollFromMouse(mouseY, choicesStartY, scrollTrackHeight);
                    return true;
                }
            }

            if (hoveredChoice >= 0 && hoveredChoice < choices.size()) {
                OpenDialoguePacket.ClientChoiceEntry entry = choices.get(hoveredChoice);
                if (entry.enabled()) {
                    playClickSound(1.0f);
                    String currentInput = inputBox != null ? inputBox.getValue() : "";
                    NetworkManager.sendToServer(new SelectChoicePacket(entry.index(), currentInput));
                    return true;
                } else {
                    playDeniedSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (choices.size() > visibleChoicesCount) {
            if (scrollY > 0) {
                if (scrollOffset > 0) {
                    scrollOffset--;
                    return true;
                }
            } else if (scrollY < 0) {
                int maxScroll = choices.size() - visibleChoicesCount;
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                    return true;
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScroll && choices.size() > visibleChoicesCount) {
            int choicesStartY = boxY + boxHeight - (visibleChoicesCount * 22) - 8;
            int scrollTrackHeight = visibleChoicesCount * 22 - 2;
            updateScrollFromMouse(mouseY, choicesStartY, scrollTrackHeight);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateScrollFromMouse(double mouseY, int trackY, int trackH) {
        int maxScroll = choices.size() - visibleChoicesCount;
        if (maxScroll <= 0) return;
        double relY = Math.max(0, Math.min(trackH, mouseY - trackY));
        scrollOffset = Math.max(0, Math.min(maxScroll, (int) Math.round((relY / trackH) * maxScroll)));
    }

    @Override
    public void onClose() {
        NetworkManager.sendToServer(new CloseDialogueC2SPacket());
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        // If typing in input box, handle input keys and Enter submission
        if (inputBox != null && inputBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                for (OpenDialoguePacket.ClientChoiceEntry entry : choices) {
                    if (entry.enabled()) {
                        playClickSound(1.0f);
                        String currentInput = inputBox.getValue();
                        NetworkManager.sendToServer(new SelectChoicePacket(entry.index(), currentInput));
                        return true;
                    }
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        // Arrow keys / Page keys to scroll choices when input is not focused
        if (choices.size() > visibleChoicesCount) {
            if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                if (scrollOffset > 0) {
                    scrollOffset--;
                    return true;
                }
            } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                int maxScroll = choices.size() - visibleChoicesCount;
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                    return true;
                }
            }
        }

        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER) {
            if (!typingComplete) {
                typingComplete = true;
                revealedChars = fullText.getString().length();
                playClickSound(1.2f);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER && hoveredChoice >= 0 && hoveredChoice < choices.size()) {
                OpenDialoguePacket.ClientChoiceEntry entry = choices.get(hoveredChoice);
                if (entry.enabled()) {
                    playClickSound(1.0f);
                    String currentInput = inputBox != null ? inputBox.getValue() : "";
                    NetworkManager.sendToServer(new SelectChoicePacket(entry.index(), currentInput));
                    return true;
                } else {
                    playDeniedSound();
                    return true;
                }
            }
        }

        // Numeric keys 1..9 (only when input box is NOT focused)
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int selectedIndex = keyCode - GLFW.GLFW_KEY_1;
            if (selectedIndex < choices.size()) {
                OpenDialoguePacket.ClientChoiceEntry entry = choices.get(selectedIndex);
                if (entry.enabled()) {
                    playClickSound(1.0f);
                    String currentInput = inputBox != null ? inputBox.getValue() : "";
                    NetworkManager.sendToServer(new SelectChoicePacket(entry.index(), currentInput));
                    return true;
                } else {
                    playDeniedSound();
                    return true;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void playClickSound(float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch));
    }

    private void playDeniedSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.DISPENSER_FAIL, 0.9f));
    }
}
