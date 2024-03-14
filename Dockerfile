# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:8.5 AS build

WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon

FROM openjdk:17
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar","app.jar"]