# =====================================================================
# Многоэтапная сборка: сначала собираем jar-файл (нужен Maven + JDK),
# затем кладём только готовый jar в лёгкий образ с JRE — итоговый
# образ получается маленьким и не тащит за собой Maven и исходники.
# =====================================================================

# ---- Этап 1: сборка проекта ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Сначала копируем только pom.xml и скачиваем зависимости — если менять
# будем только код (а не список зависимостей), Docker переиспользует этот
# слой из кэша, и сборка будет намного быстрее.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Этап 2: образ для запуска ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /build/target/dance-studio.jar ./dance-studio.jar

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "dance-studio.jar"]
