# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .

# 윈도우 줄바꿈 문제를 해결하기 위해 dos2unix 설치 및 변환
RUN apk add --no-cache dos2unix && \
    dos2unix gradlew && \
    chmod +x ./gradlew

RUN ./gradlew clean bootJar -x test

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 환경 변수 설정
ENTRYPOINT ["java", "-jar", "app.jar"]