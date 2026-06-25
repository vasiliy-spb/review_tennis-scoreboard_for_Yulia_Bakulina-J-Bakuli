### [Ревью на реализацию проекта находится здесь](https://github.com/vasiliy-spb/review_tennis-scoreboard_for_Yulia_Bakulina-J-Bakuli/blob/code-review/code-review/REVIEW_README.md)

# Tennis Match Scoreboard

Небольшое веб-приложение для ведения счёта в теннисном матче. Можно создать матч, накидывать очки и посмотреть список уже сыгранных.

Стек: Java 17, Servlets, JSP, Hibernate, H2.

## Запуск приложения

Нужны Java 17, Maven и Tomcat 10+.

Скопируй конфиг БД:

```bash
cp src/main/resources/hibernate.cfg.xml.example src/main/resources/hibernate.cfg.xml
```

По умолчанию H2 в режиме in-memory.
Текущие матчи хранятся в памяти приложения. Завершённые — в H2. После перезапуска Tomcat незавершённые матчи пропадут.

## Сборка

```bash
mvn clean package
```

```bash
mvn test
```

После сборки war лежит здесь: `target/TennisMatchScoreboard.war`

## Локальный запуск через Tomcat

1. Собери проект: `mvn clean package`
2. Скопируй war в папку `webapps` своего Tomcat.
3. Запусти Tomcat.
4. Открой в браузере:

```
http://localhost:8080/TennisMatchScoreboard/home
```

### IntelliJ IDEA

1. Open → папка с `pom.xml`
2. Project SDK — Java 17
3. Lombok включён, annotation processing тоже (Settings → Compiler → Annotation Processors)
4. Run → Edit Configurations → Tomcat Server → Local
5. Во вкладке Deployment добавь war-артефакт
6. Запусти — IDEA сама подставит context path

### Демо

- Tennis: `http://31.56.208.168:8080/tennis/home`