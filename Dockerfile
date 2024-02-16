FROM alpine:latest as get-deps

ARG ANT_VERSION=1.10.14

RUN apk add --no-cache unzip curl && \ 
    curl -o builder.zip "https://dlcdn.apache.org/ant/binaries/apache-ant-${ANT_VERSION}-bin.zip" && \
    unzip builder.zip -d /opt/builder/

FROM ghcr.io/graalvm/jdk-community:21 as builder

WORKDIR /app

ARG ANT_VERSION=1.10.14

COPY --from=get-deps /opt/builder/ /opt/builder/
COPY ./meta ./meta
COPY build.xml build.xml
COPY ./src ./src
COPY ./lib ./lib

RUN ls -la /opt/builder/apache-ant-${ANT_VERSION}

ENV PATH="/opt/builder/apache-ant-${ANT_VERSION}/bin:${PATH}"

RUN ant compile jar && \
    java --version

FROM ghcr.io/graalvm/native-image-community:21 as builder-native

WORKDIR /app

COPY --from=builder /app/build/jar/App.jar App.jar

RUN native-image --static -jar App.jar --no-fallback --strict-image-heap -march=native && chmod +x ./App

FROM alpine:latest as runner

WORKDIR /app

COPY --from=builder-native /app/App /usr/local/bin/App

RUN chmod +x /usr/local/bin/App

EXPOSE 8080

ENTRYPOINT [ "/usr/local/bin/App" ]