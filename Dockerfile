FROM openjdk:17
WORKDIR /app
COPY ./target/demo-0.0.1-SNAPSHOT.jar /app/
EXPOSE 8081
CMD java -jar demo-0.0.1-SNAPSHOT.jar

#demo-0.0.1-SNAPSHOT.jar