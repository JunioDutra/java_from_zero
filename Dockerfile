FROM ghcr.io/graalvm/jdk-community:21 as builder

ARG ANT_VERSION=1.10.14

WORKDIR /app

COPY build.xml build.xml
COPY ./src ./src
COPY ./lib ./lib

ADD http://mirror.centos.org/centos/7/os/x86_64/Packages/unzip-6.0-21.el7.x86_64.rpm /tmp

ENV PATH="/opt/builder/apache-ant-${ANT_VERSION}/bin:${PATH}"

RUN rpm -i /tmp/unzip-6.0-21.el7.x86_64.rpm && \
    curl -o builder.zip "https://dlcdn.apache.org/ant/binaries/apache-ant-${ANT_VERSION}-bin.zip" && \
    unzip builder.zip -d /opt/builder/ && \
    ant compile jar && \
    java --version

FROM ghcr.io/graalvm/jdk-community:21 as runner

WORKDIR /app

COPY --from=builder /app/build/jar/App.jar App.jar

EXPOSE 8080

CMD ["java", "-jar", "App.jar"]