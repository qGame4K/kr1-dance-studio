# Шпаргалка к защите

Короткие ответы на пункты «на защите необходимо уметь объяснить». Подробнее и по ролям —
в `docs/МЕТОДИЧКА.docx`.

**Назначение основных классов**
`Client`, `DanceClass`, `Enrollment` — сущности (что храним). `Repository` и три его реализации —
чтение/запись в PostgreSQL. `ClientService`, `DanceClassService`, `EnrollmentService`,
`StatisticsService`, `ExportService` — вся логика и проверки. `ConsoleApp`, `ConsoleInput` — меню
и ввод. `Main` — собирает всё вместе и запускает.

**Инкапсуляция**
Все поля классов в `model/` — `private`. Снаружи их можно менять только через
геттеры/сеттеры, а создание/изменение объектов идёт через сервисы, где стоит проверка
данных (`validate(...)` в `ClientService`, `DanceClassService`).

**Интерфейс**
`Repository<T>` (`repository/Repository.java`) — 5 методов CRUD (`save`, `findAll`, `findById`,
`update`, `delete`). Его реализуют `ClientRepository`, `DanceClassRepository`,
`EnrollmentRepository` — каждый под свою таблицу.

**Полиморфизм**
Сервисы хранят поля типа `Repository<Client>`, `Repository<DanceClass>`, `Repository<Enrollment>`,
а не конкретные классы. В `Main.java` туда передаются конкретные объекты
(`new ClientRepository()` и т.д.) — один и тот же код сервиса работает с любой реализацией
интерфейса, не зная, какая именно за ним стоит.

**Enum**
`DanceStyle`, `DanceLevel`, `EnrollmentStatus` — фиксированные наборы значений вместо
произвольных строк. `EnrollmentStatus` — 4 значения (`CREATED`, `CONFIRMED`, `COMPLETED`,
`CANCELLED`), переходы между ними проверяет `EnrollmentService.changeStatus(...)`.

**Коллекции**
`List<...>` — списки клиентов/занятий/записей. `Map<EnrollmentStatus, Set<EnrollmentStatus>>` —
таблица разрешённых переходов статуса. `Map<EnrollmentStatus, Long>` — статистика по статусам.
Используется Stream API: `filter`, `map`, `sorted`, `reduce`, `toList` — поиск, фильтрация,
сортировка и подсчёт выручки сделаны через потоки, а не ручными циклами со счётчиками.

**Обработка исключений**
Свой класс `DanceStudioException` (unchecked) и два наследника: `ValidationException`
(некорректные данные/нарушение бизнес-правила) и `NotFoundException` (нет записи с таким id).
Все они ловятся один раз в `ConsoleApp.run()` — программа печатает ошибку и не падает.

**JDBC и работа с базой**
`Database.connect()` открывает `Connection` через `DriverManager` (адрес/логин/пароль — из
переменных окружения `DB_URL`/`DB_USER`/`DB_PASSWORD`, которые задаёт `docker-compose.yml`).
Каждый репозиторий открывает соединение в `try-with-resources` — оно закрывается автоматически.

**Statement vs PreparedStatement**
`Statement` выполняет SQL как есть — годится только для запросов без пользовательских
значений (`findAll()`, обычный `SELECT * FROM ...`). `PreparedStatement` — SQL-шаблон
с `?`, значения подставляются отдельно (`setString`, `setLong`, ...) и никогда не попадают
в текст запроса как есть. Все запросы с пользовательскими данными (`save`, `update`,
`findById`, `delete`) — только `PreparedStatement`, это защита от SQL-инъекций.

**Связи между таблицами**
`clients (1) --- (N) enrollments (N) --- (1) dance_classes`. Внешние ключи
`enrollments.client_id → clients.id` и `enrollments.class_id → dance_classes.id`
(см. `sql/01_schema.sql`, схема — `docs/er-diagram.png`).

**Бизнес-правила** (все — в `EnrollmentService`)
1. Клиент должен существовать. 2. Занятие должно существовать. 3. Возраст клиента ≥
минимального возраста занятия. 4. Нельзя записаться на одно занятие дважды (пока запись
активна). 5. Нельзя записаться, если нет свободных мест. 6. Нельзя перевести запись в
статус, не разрешённый из текущего (`CREATED → CONFIRMED/CANCELLED`,
`CONFIRMED → COMPLETED/CANCELLED`, из `COMPLETED`/`CANCELLED` — никуда). 7. Нельзя
подтвердить/завершить неоплаченную запись.

**Создание, изменение, удаление записей**
Пункты меню «Записи на занятия» → 1 (создать), 4 (статус), 5 (оплата), 6 (удалить).
Всё проходит через `EnrollmentService`, ошибки — через `ValidationException`/`NotFoundException`.

**Поиск, фильтрация, сортировка**
Поиск — по имени клиента и по названию занятия (`EnrollmentService.searchByClientName/
searchByClassTitle`). Фильтрация — по статусу и по оплате (`filterByStatus/filterByPaid`).
Сортировка — по дате создания и по цене (`sortByDateCreated/sortByPrice`). Всё — Stream API.

**Экспорт данных**
`ExportService.exportToExcel(...)` (Apache POI) создаёт `.xlsx` с тремя листами — «Клиенты»,
«Занятия», «Записи» — в папке `export/`.
