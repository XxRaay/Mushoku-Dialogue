# 💬 Mushoku Dialogue API

**Mushoku Dialogue API** is a lightweight, modern, and developer-friendly dialogue engine and interactive NPC library for Minecraft 1.21.1 (Fabric & NeoForge), built on [Architectury API](https://architectury.dev/).

It provides a declarative Java Builder API, server-authoritative network validation, and an atmospheric RPG interface complete with cinematic typewriter text, keyboard navigation, and custom input fields.

---

## ✨ Features

- 🌲 **Branching Dialogue Trees**: Declarative tree structure (`DialogueTree`, `DialogueNode`, `DialogueChoice`, `goTo`, loops, sequences, and closures).
- 🎨 **Atmospheric RPG GUI**:
  - Antique bronze-gold framing with an ornate speaker nameplate (`❖ Name ❖`).
  - **No Background Blur**: Keeps the Minecraft world and NPC in crystal-clear view (no blurry depth-of-field obstruction).
  - Smooth typewriter animation (instantly skippable with Mouse Click, `Space`, or `Enter`).
  - Quick option selection via number keys (`1`–`9`) or mouse.
  - Interactive scrollbar support for dialogue nodes with many choices (drag thumb, mouse wheel, or Arrow/Page keys).
- 🔒 **Conditional Choices & Tooltips**:
  - Gray out unavailable options with lock icons (`🔒`) and custom informational tooltips (e.g. *Requires 32 Emeralds* or *Missing Quest Item*), or hide them entirely.
- ⌨️ **Player Text Input (`EditBox`)**:
  - Built-in text input directly inside the dialogue interface for riddles, secret passwords, naming, or entering custom quantities.
- 💾 **Stateful Session Context (`DialogueContext`)**:
  - Store and retrieve transient session variables across multiple conversation nodes.
- 🤝 **Dual Entity Support**:
  - Implement `IDialogueHolder` on custom entity classes.
  - Attach dialogues to any vanilla or third-party mobs using `DialogueInteractionHandler.registerEntityDialogue(...)`.
- 🛡️ **Server-Authoritative & Safe**:
  - All player choices are validated on the server side. Dialogues automatically terminate safely if the entity dies or the player walks away (> 8 blocks).

---

## 🚀 Quick Start Example

```java
DialogueTree tree = DialogueTree.builder("elder_quest")
    .startNode("greeting")
    .node("greeting", node -> node
        .speaker("Village Elder")
        .text("Greetings, traveler! What brings you to our humble village?")
        .choice("ask_quest", choice -> choice
            .text("I am looking for work. Any bounties?")
            .action(ChoiceAction.goTo("quest_info"))
        )
        .choice("farewell", choice -> choice
            .text("Just passing by. Farewell.")
            .action(ChoiceAction.close())
        )
    )
    .node("quest_info", node -> node
        .speaker("Village Elder")
        .text("Goblins have been spotted near the border. Bring me 5 emeralds and I will reward you handsomely!")
        .choice("accept", choice -> choice
            .text("Consider it done!")
            .action(ChoiceAction.sequence(
                ChoiceAction.run(player -> player.sendSystemMessage(Component.literal("§a[Quest Accepted]"))),
                ChoiceAction.close()
            ))
        )
        .choice("back", choice -> choice
            .text("I am not ready for that.")
            .action(ChoiceAction.goTo("greeting"))
        )
    )
    .build();

DialogueRegistry.register(tree);
```

---

## 🎮 Controls

| Input | Action |
| :--- | :--- |
| **Left Click** / **Space** / **Enter** | Skip typewriter animation |
| **Left Click** (on choice) | Select option |
| **Number Keys 1–9** | Quick-select corresponding option |
| **Scroll Wheel / Arrows** | Scroll through choices |
| **Enter** (in text input) | Submit entered text |
| **Escape** | Safely close dialogue |

---

## 📦 Requirements & Compatibility

| Platform | Loader | Minecraft | Dependencies |
| :--- | :--- | :--- | :--- |
| **NeoForge** | NeoForge 21.1+ | `1.21.1` | [Architectury API](https://modrinth.com/mod/architectury-api) |
| **Fabric** | Fabric Loader 0.16+ | `1.21.1` | [Fabric API](https://modrinth.com/mod/fabric-api), [Architectury API](https://modrinth.com/mod/architectury-api) |

---

## 📜 License

Licensed under the **MIT License**. Free for use in any mod, modpack, or server. See the [LICENSE](https://github.com/XxRaay/Mushoku-Dialogue/blob/master/LICENSE) file on GitHub for full details.
