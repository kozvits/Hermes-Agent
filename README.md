# Hermes Agent for Android

**Нативный Android-клиент для Hermes Agent от Nous Research.**

![Hermes Agent](app/src/main/res/drawable/ic_hermes_logo.xml)

## ✨ Возможности

- 🤖 **Чат с AI** — потоковая генерация, поддержка Markdown, отображение вызовов инструментов
- 🧠 **Память** — долговременное хранение контекста между сессиями
- 📜 **История сессий** — просмотр, переименование и удаление сессий
- ⚡ **Навыки** — управление навыками Hermes (включение/отключение)
- ⏰ **Cron задачи** — создание и управление автоматическими задачами по расписанию
- ☁️ **Провайдеры** — поддержка OpenRouter, Anthropic, OpenAI, DeepSeek, Google Gemini и 20+ других
- 🔧 **Инструменты** — управление инструментами (терминал, веб, файлы, код и другие)
- 🎨 **Material 3** — современный дизайн с поддержкой Material You (Dynamic Colors)
- 🌙 **Тёмная тема** — полная поддержка тёмной и светлой темы
- 🔌 **Подключение к серверу** — работа через REST API + SSE (Server-Sent Events)

## 📸 Скриншоты

(Добавьте скриншоты после сборки)

## 🛠 Технологический стек

| Технология | Назначение |
|------------|------------|
| **Kotlin** | Язык программирования |
| **Jetpack Compose** | UI (Material 3) |
| **Hilt** | Внедрение зависимостей |
| **Retrofit + OkHttp** | REST API |
| **OkHttp SSE** | Потоковая передача событий |
| **Room** | Локальная БД |
| **DataStore** | Настройки приложения |
| **Navigation Compose** | Навигация |
| **Coroutines + Flow** | Асинхронность |

## 📋 Архитектура

```
com.nousresearch.hermesagent/
├── di/                    # Hilt модули
├── data/
│   ├── api/               # Retrofit сервис, SSE клиент, модели
│   ├── local/             # Room БД, DataStore преференсы
│   └── repository/        # Репозитории
├── navigation/            # NavHost, маршруты
├── service/               # Foreground сервис WebSocket
├── ui/
│   ├── theme/             # Material 3 тема, цвета, типографика
│   ├── components/        # Переиспользуемые композитные функции
│   └── screens/           # Экраны (chat, settings, sessions, skills, cron, providers, tools, memory)
├── HermesApplication.kt   # Application class (Hilt)
└── MainActivity.kt        # Точка входа
```

## 🚀 Запуск

### Требования

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17+
- Android SDK 35
- Gradle 8.10+

### Установка

1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/NousResearch/hermes-agent-android.git
   cd hermes-agent-android
   ```

2. **Откройте в Android Studio:**
   `File → Open → выберите папку проекта`

3. **Синхронизируйте Gradle:**
   Дождитесь завершения синхронизации

4. **Запустите на эмуляторе или устройстве:**
   Нажмите `Run` ▶

### Подключение к серверу Hermes

1. Запустите Hermes Agent на вашем компьютере:
   ```bash
   hermes gateway run
   ```

2. В приложении укажите URL сервера:
   - **Эмулятор Android:** `http://10.0.2.2:8080`
   - **Реальное устройство:** `http://<IP-вашего-компьютера>:8080`

3. Нажмите «Подключиться»

## 🔧 API Эндпоинты

Приложение использует следующие API эндпоинты:

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/v1/chat/completions` | Чат (в т.ч. SSE streaming) |
| GET | `/api/status` | Статус сервера |
| GET | `/v1/models` | Список моделей |
| GET | `/api/providers` | Список провайдеров |
| POST | `/api/providers/activate` | Активировать провайдер |
| GET | `/api/sessions` | Список сессий |
| GET | `/api/sessions/{id}/messages` | Сообщения сессии |
| GET | `/api/skills` | Список навыков |
| GET | `/api/cron` | Список cron задач |
| GET | `/api/tools` | Список инструментов |
| GET | `/api/memory` | Записи памяти |

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку (`git checkout -b feature/amazing`)
3. Внесите изменения
4. Отправьте PR

## 📄 Лицензия

MIT License — смотрите файл [LICENSE](LICENSE)

---

**Сделано с ❤️ для Nous Research**
