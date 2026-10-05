# 🗺️ Архитектурная карта проекта: MushokuDialogue

> Дата последнего обновления: 2026-10-05 | Версия карты: 1.0

## 1. Обзор проекта и технологический стек
- **Назначение:** Кроссплатформенная библиотека и API для создания интерактивных деревьев диалогов с NPC в Minecraft 1.21.1.
  - Поддерживает разветвленные диалоговые деревья (Dialogue Trees), условия доступности реплик (уровень, предметы, статус), кастомные действия (переходы между узлами, выполнение произвольного кода, открытие торговли/интерфейсов, закрытие).
  - Красивый стилизованный RPG-интерфейс в духе фэнтези (темная бронза, золото, свитки, звуковые эффекты клика и отказа, посимвольное появление текста).
  - Удобный Fluent Builder API для простой интеграции в любой мод/аддон.
  - Автоматическая поддержка взаимодействия на ПКМ через интерфейс `IDialogueHolder` и глобальный регистратор `DialogueInteractionHandler`.
- **Стек:**
  - Язык: **Java 21**
  - Базовая игра: **Minecraft 1.21.1** (Mojang Official Mappings)
  - Архитектура: **Architectury API 13.0.6** (Multi-Loader: Common + NeoForge 21.1.235 + Fabric 0.102.0+1.21.1)
  - Сборка: **Gradle 8.13**, **Architectury Loom 1.10.455**, **Architectury Plugin 3.4.164**
- **Точка входа (Entrypoint):**
  - Common: `Common/src/main/java/com/mushokuaddons/dialogue/MushokuDialogueCommon.java`
  - NeoForge: `NeoForge_1.21.1/src/main/java/com/mushokuaddons/dialogue/neoforge/MushokuDialogueNeoForge.java`
  - Fabric: `Fabric_1.21.1/src/main/java/com/mushokuaddons/dialogue/fabric/MushokuDialogueFabric.java`

---

## 2. Иерархическая структура каталогов и файлов
```text
MushokuDialogue/
├── .gemini/
│   └── map/
│       └── map.md                                      # Архитектурная карта библиотеки
├── Common/                                             # Ядро библиотеки
│   └── src/main/java/com/mushokuaddons/dialogue/
│       ├── MushokuDialogueCommon.java                  # Точка инициализации
│       ├── api/                                        # Публичный API
│       │   ├── ChoiceAction.java                       # Действия: goTo, run, trade, close, sequence
│       │   ├── DialogueChoice.java                     # Реплика/вариант ответа с условием и тултипом
│       │   ├── DialogueContext.java                    # Контекст сессии (игрок, энтити, текущий узел)
│       │   ├── DialogueNode.java                       # Узел диалога (говорящий, текст, реплики)
│       │   ├── DialogueRegistry.java                   # Глобальный реестр деревьев по ID
│       │   ├── DialogueTree.java                       # Дерево диалога с Builder API
│       │   └── IDialogueHolder.java                    # Интерфейс для сущностей с диалогом
│       ├── manager/                                    # Логика исполнения
│       │   ├── DialogueInteractionHandler.java         # Перехват ПКМ по сущностям
│       │   └── DialogueManager.java                    # Менеджер активных сессий диалогов игроков
│       ├── network/                                    # Сеть Architectury
│       │   ├── CloseDialoguePacket.java                # Закрытие экрана диалога
│       │   ├── DialogueNetworking.java                 # Регистрация пакетов
│       │   ├── OpenDialoguePacket.java                 # Открытие узла на клиенте
│       │   └── SelectChoicePacket.java                 # Выбор варианта ответа
│       └── client/                                     # Клиентский рендер
│           ├── gui/
│           │   └── DialogueScreen.java                 # Фэнтези экран диалога с плавным текстом и кнопками
│           └── network/
│               └── ClientDialogueHandler.java          # Клиентский диспетчер открытия/закрытия экрана
├── NeoForge_1.21.1/                                    # Модуль NeoForge
└── Fabric_1.21.1/                                      # Модуль Fabric
```

---

## 3. Журнал структурных изменений
- **2026-10-05:** Первоначальное создание библиотеки MushokuDialogue и архитектурной карты.
