FROM openjdk:23-jdk-slim
WORKDIR /app

# 컨테이너 내부에 logs 디렉토리 생성
RUN mkdir -p /app/logs

# 필요한 툴 설치
RUN apt-get -y update && apt-get install -y \
    wget \
    unzip \
    curl \
    tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo "Asia/Seoul" > /etc/timezone

# Chrome 다운로드 및 설치
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt -y install ./google-chrome-stable_current_amd64.deb && \
    rm ./google-chrome-stable_current_amd64.deb

# 확장자 파일이 포함된 폴더를 이미지에 복사
COPY src/main/resources/extension /app/extension

# 미리 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/TRASS_AUTOMATION-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
