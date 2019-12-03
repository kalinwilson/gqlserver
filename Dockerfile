FROM openjdk:8-jdk-alpine
MAINTAINER "Kalin Wilson"
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-Xms256m", "-Xmx1024m", "-Djava.net.preferIPv4Stack=true", "-Djava.security.egd=file:///dev/urandom", "-cp","app:app/lib/*","graphql.server.GqlserverApplication"]
