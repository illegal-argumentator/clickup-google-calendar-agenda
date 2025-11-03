FROM gradle:8.10.2-jdk17 AS build
WORKDIR /build

COPY build.gradle settings.gradle ./

COPY gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies || gradle dependencies

COPY src ./src

RUN ./gradlew clean bootJar -x test || gradle clean bootJar -x test

RUN ls -lh build/libs

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 4846

CMD ["java", "-jar", "app.jar"]
