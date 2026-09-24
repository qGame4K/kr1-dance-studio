# Танцевальная студия — консольное приложение (КР №1)

Консольная информационная система на **Java 21 + JDBC + PostgreSQL**.
Предметная область — танцевальная студия. Основная сущность — **«Запись на занятие»** (`Enrollment`),
которая связывает клиента (`Client`) и занятие (`DanceClass`).

Архитектура простая и однонаправленная:

```
Консольное меню (ui) → Сервисы с бизнес-логикой (service) → Репозитории/JDBC (repository) → PostgreSQL
```

## Что где лежит

| Что требуется | Где искать |
|---|---|
| Исходный код Java | `src/main/java/ru/mirea/dancestudio/` |
| `pom.xml` | [pom.xml](pom.xml) |
| SQL-скрипт создания БД | [sql/](sql) |
| ER-диаграмма | [docs/er-diagram.png](docs/er-diagram.png) |
| Экспорт в Excel | появляется в папке `export/` после пункта меню «Экспорт» |
| Методичка для защиты (роли, кто что делал) | `docs/МЕТОДИЧКА.docx` |
| Шпаргалка с ответами для защиты | [docs/DEFENSE.md](docs/DEFENSE.md) |

## Самый простой способ запустить — Docker

Нужен только установленный **Docker Desktop** (Windows/macOS/Linux) — ни Java, ни Maven,
ни PostgreSQL ставить не нужно, всё будет внутри контейнеров.

```bash
git clone <ссылка-на-ваш-репозиторий>
cd <папка-проекта>
```

Дальше — один из вариантов:

* **macOS / Linux:** `./start.sh`
* **Windows:** двойной клик по `start.bat` (или `start.bat` в командной строке)
* **Вручную** (то же самое, что делают скрипты):
  ```bash
  docker compose up -d db
  docker compose run --rm app
  ```

Первый запуск скачает образы и соберёт проект — это может занять пару минут.
При старте автоматически создаётся база `dance_studio`, таблицы и тестовые данные
(8 клиентов, 10 занятий, 16 записей). Дальше откроется меню программы прямо в терминале.

**Остановить базу данных**, когда закончили: `docker compose down` (данные останутся,
следующий `docker compose up -d db` поднимет ту же базу). Чтобы стереть всё и начать заново:
`docker compose down -v`.

**Экспорт в Excel:** файл появится не только внутри контейнера, но и в папке `export/`
на вашем компьютере — её можно сразу сдавать в качестве deliverable.

## Запуск без Docker (локально)

Нужны: JDK 21+, Maven 3.9+, локальный PostgreSQL 14+.

1. Создайте базу данных и выполните SQL-скрипты по порядку:
   ```bash
   psql -U postgres -f sql/00_create_database.sql
   psql -U postgres -d dance_studio -f sql/01_schema.sql
   psql -U postgres -d dance_studio -f sql/02_test_data.sql
   ```
2. Если пароль/пользователь Postgres отличаются от `postgres`/`postgres`, задайте переменные окружения:
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/dance_studio
   export DB_USER=postgres
   export DB_PASSWORD=ваш_пароль
   ```
3. Соберите и запустите:
   ```bash
   mvn clean package
   java -jar target/dance-studio.jar
   ```
   Или без сборки jar-файла: `mvn compile exec:java`.

## Структура кода

```
src/main/java/ru/mirea/dancestudio/
├── Main.java                 — точка входа, собирает всё вместе
├── model/                    — сущности: Client, DanceClass, Enrollment, перечисления
├── exception/                — свои исключения (DanceStudioException и наследники)
├── db/                       — подключение к PostgreSQL (Database.java)
├── repository/                — CRUD-запросы к базе данных (интерфейс Repository<T> + 3 реализации)
├── service/                  — бизнес-правила, поиск/фильтрация/сортировка, статистика, экспорт
└── ui/                       — консольное меню (ConsoleApp) и чтение ввода (ConsoleInput)
```

Подробное распределение по участникам команды и объяснение решений — в `docs/МЕТОДИЧКА.docx`.
