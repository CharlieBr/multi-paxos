FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/node-0.0.1-SNAPSHOT.jar app.jar
ENV NODE_ARGS = ""
ENTRYPOINT exec java -jar /app.jar $NODE_ARGS