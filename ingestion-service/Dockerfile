FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

ARG APP_VERSION
ARG APP_PORT

COPY common/common-kafka/pom.xml common/common-kafka/pom.xml
COPY common/common-kafka/src common/common-kafka/src

COPY common/common-security/pom.xml common/common-security/pom.xml
COPY common/common-security/src common/common-security/src

COPY common/common-web/pom.xml common/common-web/pom.xml
COPY common/common-web/src common/common-web/src

RUN mvn -f common/common-kafka/pom.xml clean install -DskipTests

RUN mvn -f common/common-security/pom.xml clean install -DskipTests

RUN mvn -f common/common-web/pom.xml clean install -DskipTests

COPY ingestion-service/pom.xml .
COPY ingestion-service/src ./src

RUN mvn clean package \
    -DskipTests \
    -Drevision=${APP_VERSION}


FROM eclipse-temurin:17-jre

WORKDIR /app

ARG APP_VERSION
ARG APP_PORT

COPY --from=build \
    /app/target/ingestion-service-${APP_VERSION}.jar \
    app.jar

EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]