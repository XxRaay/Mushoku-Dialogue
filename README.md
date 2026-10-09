# Mushoku Dialogue API

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://minecraft.net/)
[![Platform](https://img.shields.io/badge/Platform-Fabric%20%7C%20NeoForge-blue.svg)](https://architectury.dev/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Mushoku Dialogue** — это гибкая, легковесная и современная мультиплатформенная библиотека (Fabric & NeoForge 1.21.1) для создания разветвлённых диалоговых систем, квестовых бесед и взаимодействия с NPC в Minecraft.

Она построена на базе [Architectury API](https://architectury.dev/) и предоставляет декларативный Builder API, серверно-валидируемую сетевую синхронизацию, а также стильный RPG GUI с кинематографичным эффектом печатающейся строки (typewriter).

---

## ✨ Основные возможности

- 🌲 **Древовидная структура диалогов**: ноды (`DialogueNode`), переходы между ними (`goTo`), циклические и ветвящиеся диалоги.
- 🎨 **Атмосферный RPG GUI**:
  - Кастомная рамка в золотисто-бронзовом стиле и бейдж с именем собеседника (`❖ Имя ❖`).
  - Эффект плавной печати текста (typewriter), который можно мгновенно завершить кликом, `Space` или `Enter`.
  - **Без размытия мира**: в отличие от стандартного меню 1.21, мир и NPC не размываются («нет эффекта зрения -5»).
  - Быстрый выбор вариантов кнопками клавиатуры `1`–`9` или кликом мыши.
- 🔒 **Условные выборы и подсказки (Conditions & Tooltips)**:
  - Возможность скрывать реплики или показывать их заблокированными (с замочком 🔒 и подсказкой, почему вариант недоступен — например, не хватает золота или уровня).
- ⌨️ **Ввод текста от игрока (EditBox Input)**:
  - Встроенное поле ввода прямо в диалоге (для ввода имён, паролей, загадок или сумм).
- 💾 **Сессионный контекст (`DialogueContext`)**:
  - Хранение временных переменных и состояния на протяжении всего разговора с игроком.
- 🤝 **Интеграция с сущностями (NPC & Vanilla)**:
  - Реализация интерфейса `IDialogueHolder` у своих кастомных мобов.
  - Подключение диалогов к любым ванильным или сторонним сущностям через `DialogueInteractionHandler.registerEntityDialogue(...)`.
- 🛡️ **Безопасность и серверная валидация**:
  - Все действия проверяются на сервере.
  - Автоматическое закрытие диалога при смерти NPC или отдалении игрока (> 8 блоков).

---

## 📦 Подключение к проекту

### Gradle (Loom / Architectury)

#### 1. Добавление репозитория
Если библиотека опубликована локально (`mavenLocal()`) или в репозитории:

```groovy
repositories {
    mavenLocal()
    // или Maven репозиторий вашего проекта/организации
}
```

#### 2. Зависимости в подпроектах

##### В Common-модуле:
```groovy
dependencies {
    modApi "com.mushokuaddons.dialogue:mushokudialogue-common:1.0.0"
}
```

##### В Fabric-модуле:
```groovy
dependencies {
    modImplementation "com.mushokuaddons.dialogue:mushokudialogue-fabric:1.0.0"
    // Не забудьте architectury и fabric-api
    modApi "dev.architectury:architectury-fabric:13.0.6"
}
```

##### В NeoForge-модуле:
```groovy
dependencies {
    modImplementation "com.mushokuaddons.dialogue:mushokudialogue-neoforge:1.0.0"
    modApi "dev.architectury:architectury-neoforge:13.0.6"
}
```

##### Зависимость в `fabric.mod.json`:
```json
"depends": {
  "mushokudialogue": ">=1.0.0"
}
```

##### Зависимость в `neoforge.mods.toml`:
```toml
[[dependencies.yourmodid]]
modId="mushokudialogue"
type="required"
versionRange="[1.0.0,)"
ordering="NONE"
side="BOTH"
```

---

## 🚀 Руководство по использованию в коде

### 1. Архитектура и основные классы

| Класс / Интерфейс | Описание |
|---|---|
| `DialogueTree` | Дерево диалога. Содержит корневую ноду и набор всех нод диалога. |
| `DialogueNode` | Отдельная реплика/экран NPC: имя говорящего, текст, список вариантов выбора, поле ввода, коллбек `onOpen`. |
| `DialogueChoice` | Вариант ответа игрока с условием доступности (`condition`), видимостью и действием (`ChoiceAction`). |
| `ChoiceAction` | Действие при выборе: переход к ноде (`goTo`), выполнение лямбды (`run`), цепочка (`sequence`), закрытие (`close`). |
| `DialogueContext` | Контекст текущего сеанса: ссылка на `player`, `entity`, `tree`, сохранение пользовательских переменных (`get`/`set`) и ввод текста (`input()`). |
| `DialogueRegistry` | Глобальный реестр деревьев диалогов (`DialogueRegistry.register(...)`). |
| `DialogueManager` | Менеджер сессий: открытие диалогов (`openDialogue`), смена ноды (`transitionToNode`), закрытие (`closeDialogue`). |
| `DialogueInteractionHandler` | Слушатель ПКМ по мобам и привязка диалогов к классам сущностей. |
| `IDialogueHolder` | Интерфейс для сущностей, возвращающих дерево диалога при клике. |

---

### 2. Создание простого диалога (Hello World)

Построим диалог с развилкой и закрытием:

```java
import com.mushokuaddons.dialogue.api.ChoiceAction;
import com.mushokuaddons.dialogue.api.DialogueChoice;
import com.mushokuaddons.dialogue.api.DialogueNode;
import com.mushokuaddons.dialogue.api.DialogueTree;
import com.mushokuaddons.dialogue.api.DialogueRegistry;
import net.minecraft.network.chat.Component;

public class ModDialogues {

    public static void register() {
        DialogueTree tree = DialogueTree.builder("tutorial_dialogue")
            .startNode("greeting")
            // Начальная нода
            .node("greeting", node -> node
                .speaker("Старейшина")
                .text("Приветствую тебя, путник! Что привело тебя в нашу деревню?")
                .choice("ask_quest", choice -> choice
                    .text("Я ищу работу. Есть ли поручения?")
                    .action(ChoiceAction.goTo("quest_info"))
                )
                .choice("say_goodbye", choice -> choice
                    .text("Я просто осматриваюсь. До свидания.")
                    .action(ChoiceAction.close())
                )
            )
            // Вторая нода
            .node("quest_info", node -> node
                .speaker("Старейшина")
                .text("Окрестные леса кишат гоблинами. Принеси мне 5 изумрудов, и я тебя награжу!")
                .choice("accept", choice -> choice
                    .text("Договорились, я вернусь с изумрудами.")
                    .action(ChoiceAction.sequence(
                        ChoiceAction.run(player -> {
                            player.sendSystemMessage(Component.literal("§a[Квест принят] Зачистите окрестности!"));
                        }),
                        ChoiceAction.close()
                    ))
                )
                .choice("back", choice -> choice
                    .text("Я пока не готов к такому.")
                    .action(ChoiceAction.goTo("greeting"))
                )
            )
            .build();

        // Регистрируем диалог в реестре
        DialogueRegistry.register(tree);
    }
}
```

---

### 3. Условия выбора и заблокированные реплики (Conditions & Tooltips)

Вы можете заблокировать реплику, если игрок не соответствует условию (например, недостаточно изумрудов), и показать ему подсказку:

```java
import net.minecraft.world.item.Items;

node.choice("buy_sword", choice -> choice
    .text("Купить меч странника (32 Изумруда)")
    // Условие: наличие у игрока 32 изумрудов
    .condition(player -> player.getInventory().countItem(Items.EMERALD) >= 32)
    // Показывать ли кнопку затемнённой с замком 🔒, если условие не выполнено
    .visibleWhenDisabled(true)
    // Тултип при наведении на заблокированную реплику
    .disabledTooltip(Component.literal("§cУ вас недостаточно изумрудов (требуется: 32)"))
    // Действие при клике
    .action(ChoiceAction.run((player, context) -> {
        player.getInventory().clearOrCountMatchingItems(p -> p.is(Items.EMERALD), 32, player.inventoryMenu.getCraftSlots());
        // Выдаём меч и переходим дальше
        ChoiceAction.goTo("purchase_success").execute(player, context);
    }))
);
```

> **Поведение:**
> - Если `visibleWhenDisabled(false)` (по умолчанию), недоступный выбор **полностью скрывается**.
> - Если `visibleWhenDisabled(true)`, кнопка отображается серым цветом с иконкой `🔒`. При попытке клика проигрывается глухой звук ошибки (`DISPENSER_FAIL`) и отображается тултип.

---

### 4. Ввод текста от игрока (Text Input Box)

Ноды диалога могут содержать поле ввода `EditBox` (например, для проверки пароля, загадок или ввода суммы перевода):

```java
DialogueTree tree = DialogueTree.builder("riddle_dialogue")
    .startNode("riddle")
    .node("riddle", node -> node
        .speaker("Сфинкс")
        .text("Назови тайное слово, чтобы пройти дальше:")
        // Включаем инпут: (Placeholder, Начальное значение, Макс. длина)
        .input("Введите секретное слово...", "", 20)
        .choice("submit", choice -> choice
            .text("Ответить")
            // Получаем введённый игроком текст
            .action(ChoiceAction.runWithInput((player, context, input) -> {
                if ("сезам".equalsIgnoreCase(input.trim())) {
                    player.sendSystemMessage(Component.literal("§aВерно! Врата открываются."));
                    ChoiceAction.goTo("success").execute(player, context);
                } else {
                    player.sendSystemMessage(Component.literal("§cНеверно! Попробуй снова."));
                    ChoiceAction.goTo("riddle").execute(player, context);
                }
            }))
        )
        .choice("give_up", choice -> choice
            .text("Сдаться")
            .action(ChoiceAction.close())
        )
    )
    .node("success", node -> node
        .speaker("Сфинкс")
        .text("Ты доказал свою мудрость. Проходи.")
        .choice("ok", choice -> choice.text("Спасибо").action(ChoiceAction.close()))
    )
    .build();
```

---

### 5. Сохранение состояния в `DialogueContext`

Каждая сессия диалога имеет объект `DialogueContext`. Вы можете сохранять в нём данные между нодами:

```java
node.choice("select_warrior", choice -> choice
    .text("Выбрать путь Воина")
    .action(ChoiceAction.sequence(
        ChoiceAction.run((player, context) -> {
            // Сохраняем выбранный класс
            context.set("chosen_class", "WARRIOR");
        }),
        ChoiceAction.goTo("confirm_choice")
    ))
);

// В следующей ноде можно получить сохранённые данные в onOpen:
node.onOpen(context -> {
    String chosen = context.get("chosen_class");
    context.player().sendSystemMessage(Component.literal("Выбран: " + chosen));
});
```

---

### 6. Привязка диалогов к сущностям (NPC)

Существует **два удобных способа** привязать диалог к мобам:

#### Способ А: Реализация `IDialogueHolder` (Для своих Entity)

Если вы создаёте собственный класс NPC:

```java
import com.mushokuaddons.dialogue.api.IDialogueHolder;
import com.mushokuaddons.dialogue.api.DialogueTree;
import com.mushokuaddons.dialogue.api.DialogueRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

public class QuestGiverEntity extends PathfinderMob implements IDialogueHolder {

    public QuestGiverEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    @Nullable
    public DialogueTree getDialogueTree(ServerPlayer player) {
        // Можно возвращать разные деревья в зависимости от квестов игрока:
        return DialogueRegistry.get("quest_giver_tree");
    }
}
```
*При клике ПКМ основной рукой библиотека автоматически откроет диалог и заблокирует дальнейшее взаимодействие.*

#### Способ Б: Через `DialogueInteractionHandler` (Для ванильных или чужих мобов)

Если нужно добавить диалог ванильным жителям (`Villager`), железному голему или мобам из другого мода:

```java
import com.mushokuaddons.dialogue.manager.DialogueInteractionHandler;
import com.mushokuaddons.dialogue.api.DialogueRegistry;
import net.minecraft.world.entity.npc.Villager;

public class ModEvents {

    public static void init() {
        // Регистрируем провайдер диалога для всех жителей
        DialogueInteractionHandler.registerEntityDialogue(Villager.class, (player, villager) -> {
            // Можно проверить профессию, теги, имя моба:
            if (villager.getVillagerData().getProfession().name().equals("librarian")) {
                return DialogueRegistry.get("librarian_dialogue");
            }
            return null; // pass дальше к стандартной торговле
        });
    }
}
```

---

### 7. Открытие диалога вручную через код

Диалог можно запустить не только кликом по сущности, но и в ответ на любое игровое событие (команда, клик по блоку/предмету, вход в зону):

```java
import com.mushokuaddons.dialogue.manager.DialogueManager;
import net.minecraft.server.level.ServerPlayer;

// 1. По ID зарегистрированного дерева
DialogueManager.openDialogue(serverPlayer, "tutorial_dialogue", null);

// 2. По ссылке на объект DialogueTree (сущность можно передать или оставить null)
DialogueManager.openDialogue(serverPlayer, myCustomTree, targetEntity);
```

Закрыть текущий диалог игрока программно:
```java
DialogueManager.closeDialogue(serverPlayer);
```

---

### 8. Комбинированные действия (`ChoiceAction.sequence`)

`ChoiceAction.sequence(...)` позволяет объединять несколько действий в одну транзакцию:

```java
ChoiceAction.sequence(
    ChoiceAction.run(player -> player.giveExperienceLevels(1)),
    ChoiceAction.run((player, ctx) -> player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f)),
    ChoiceAction.goTo("next_step")
)
```

---

## 🎮 Клиентский интерфейс и управление

Диалоговое окно разработано с упором на погружение в атмосферу RPG:

| Клавиша / Действие | Функция |
|---|---|
| **ЛКМ** (по экрану) | Мгновенно отобразить весь текст (если он ещё печатается) |
| **Space** / **Enter** | Пропустить анимацию печати текста |
| **ЛКМ** (по варианту) | Выбрать вариант ответа |
| **Цифры 1 – 9** | Быстрый выбор варианта ответа соответствующим номером |
| **Enter** (в поле ввода) | Автоматически отправить введённый текст по первому доступному действию |
| **Escape** | Закрыть диалог и уведомить сервер |

---

## 🛠️ Сборка проекта

Требуется установленная **Java 21**.

Собрать все артефакты (Common, Fabric, NeoForge):
```bash
./gradlew build
```

Готовые jar-файлы будут расположены в:
- `Fabric_1.21.1/build/libs/mushokudialogue-fabric-1.0.0.jar`
- `NeoForge_1.21.1/build/libs/mushokudialogue-neoforge-1.0.0.jar`

---

## 📄 Лицензия

Проект распространяется под лицензией **MIT**. Подробности в файле [LICENSE](LICENSE).
