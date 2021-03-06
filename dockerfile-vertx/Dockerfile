FROM gradle:4.10.2-jdk8-alpine as builder
ENV APP_HOME "/home/app"
COPY --chown=gradle . ${APP_HOME}
WORKDIR ${APP_HOME}
RUN gradle shadowJar

FROM openjdk:8-jdk-alpine

ENV APP_HOME "/home/app"
ENV APP "my-application.jar"
ENV APP_USER "openfaas-vertx"
ENV OF_WATCHDOG_VERSION "0.4.5"

ENV fprocess "java -jar ${APP_HOME}/${APP}"
ENV upstream_url "http://127.0.0.1:9000"
ENV mode="http"

WORKDIR ${APP_HOME}

RUN apk --no-cache add curl \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas-incubator/of-watchdog/releases/download/${OF_WATCHDOG_VERSION}/of-watchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && cp /usr/bin/fwatchdog ${APP_HOME} \
    && apk del curl --no-cache

COPY --from=builder ${APP_HOME}/build/libs/${APP} ${APP_HOME}/${APP}

RUN addgroup -S ${APP_USER} && adduser ${APP_USER} -S -G ${APP_USER} ${APP_USER}
RUN chown ${APP_USER} ${APP_HOME}
USER ${APP_USER}

EXPOSE 8080

HEALTHCHECK --interval=3s CMD [ -e /tmp/.lock ] || exit 1

CMD [ "fwatchdog" ]
