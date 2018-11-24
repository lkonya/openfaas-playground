FROM openjdk:8u121-jdk-alpine as builder

ENV GRADLE_HOME=/opt/gradle-4.10.2/
ENV PATH=$PATH:$GRADLE_HOME/bin
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

RUN apk --no-cache add curl \
    && addgroup -S app && adduser -S -g app app \
    && mkdir -p /opt/ && cd /opt/ \
    && echo "Downloading gradle.." \
    && curl -SfL https://services.gradle.org/distributions/gradle-4.10.2-bin.zip -o gradle-bin.zip \
    && unzip gradle-bin.zip -d /opt/ \
    && rm gradle-bin.zip \
    && apk del curl

WORKDIR /home/app
COPY . /home/app/
RUN gradle shadowJar

FROM openjdk:8u121-jdk-alpine

ENV JAR_NAME=my-application.jar
ENV fprocess="java -jar /home/app/$JAR_NAME"
ENV upstream_url="http://127.0.0.1:9000"

RUN mkdir -p /home/app

RUN apk --no-cache add curl \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas/faas/releases/download/0.9.11/fwatchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && cp /usr/bin/fwatchdog /home/app \
    && apk del curl --no-cache

# Add non root user
RUN addgroup -S app && adduser app -S -G app app
WORKDIR /home/app
COPY --from=builder /home/app/build/libs/$JAR_NAME /home/app/$JAR_NAME

RUN chown app /home/app
USER app

EXPOSE 8080

HEALTHCHECK --interval=3s CMD [ -e /tmp/.lock ] || exit 1
CMD [ "fwatchdog" ]

