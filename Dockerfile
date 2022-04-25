##Этап №1. Создаём jar-ник
FROM maven:3.8.5-eclipse-temurin-18 AS pack
WORKDIR /forge
COPY . ./
RUN mvn install

##Этап №2. Запускаем jar-ник
FROM openjdk:18.0.1-slim
ARG JAR_NAME="FilmsLibrary-1"
WORKDIR /application
COPY --from=pack /forge/target/${JAR_NAME}.jar /application/${JAR_NAME}.jar
EXPOSE 8081

#Устанавливаем инструмент для ожидания зависимостей
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

#Включаем ожидание зависимостей (будет проставлено в переменных окружения). После ожидания зависимостей запускаем приложение
CMD /wait && java -jar ${JAR_NAME}.jar
