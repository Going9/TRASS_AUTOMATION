FROM openjdk:23-jdk-slim
WORKDIR /app

# 컨테이너 내부에 logs 디렉토리 생성
RUN mkdir -p /app/logs

# 미리 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/TRASS_AUTOMATION-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
