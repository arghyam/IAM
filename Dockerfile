FROM gradle:4.10.2-jdk8-alpine

ADD ./src src
#ADD ./gradle/wrapper gradle/wrapper
ADD ./build.gradle   build.gradle
#ADD ./gradlew gradlew
#ADD ./gradlew.bat    gradlew.bat
ADD ./settings.gradle    settings.gradle

RUN gradle clean build -x test

ENTRYPOINT ["java","-jar", "./build/libs/Arghyam-backend-0.0.1-SNAPSHOT.jar", "-Dspring.profiles.active=dev","--spring.config.location=application.properties"]

