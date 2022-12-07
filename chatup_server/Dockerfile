FROM gradle:jdk17-alpine

WORKDIR /app

COPY . .

RUN gradle build

EXPOSE 8080
