FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

ARG APP_VERSION
ARG APP_PORT

COPY common/common-kafka/pom.xml common/common-kafka/pom.xml
COPY common/common-kafka/src common/common-kafka/src

RUN mvn -f common/common-kafka/pom.xml clean install -DskipTests


COPY processing-service/pom.xml .
COPY processing-service/src ./src

RUN mvn clean package \
    -DskipTests \
    -Drevision=${APP_VERSION}


FROM eclipse-temurin:17-jre

WORKDIR /app

ARG APP_VERSION
ARG APP_PORT

COPY --from=build \
    /app/target/processing-service-${APP_VERSION}.jar \
    app.jar

EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]