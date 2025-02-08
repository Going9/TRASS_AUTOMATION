# Dockerfile
FROM openjdk:23-jdk-slim
WORKDIR /app

# 미리 빌드된 JAR 파일을 컨테이너에 복사 (빌드 후 JAR 파일 경로에 맞게 수정)
COPY build/libs/trass-automation.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
