# Используем готовый образ JDK 17
FROM openjdk:17-jdk-slim

# Инициализация переменные окружения
ENV ANDROID_SDK_ROOT=/opt/android
ENV GRADLE_HOME=/opt/gradle/gradle-8.0.2
ENV PATH=${GRADLE_HOME}/bin:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${PATH}

# Установка нужных пакетов
RUN apt-get update && apt-get -y install wget unzip

# Установка Android SDK Tools и Gradle
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip -P /tmp && \
    mkdir -p /opt/android/cmdline-tools && \
    unzip -d /opt/android/cmdline-tools /tmp/commandlinetools-linux-8512546_latest.zip && \
    mv /opt/android/cmdline-tools/cmdline-tools /opt/android/cmdline-tools/latest && \
    rm /tmp/commandlinetools-linux-8512546_latest.zip && \
    wget https://services.gradle.org/distributions/gradle-8.0.2-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-8.0.2-bin.zip && \
    rm /tmp/gradle-8.0.2-bin.zip && \
    sdkmanager --update && \
    yes | sdkmanager --licenses  && \
    mkdir /app

COPY . /app

WORKDIR /app

# При запуске контейнера запускаем сборку приложения
CMD ["gradle", "assembleDebug"]