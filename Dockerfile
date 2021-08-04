FROM maven:3-jdk-11 as builder

RUN apt update && apt install -y git proxychains \
    && sed -i 's/socks4.*/socks5 192.168.0.12 1080/g' /etc/proxychains.conf \
    && cd /tmp \
    && proxychains git clone https://github.com/nekomew/music.git \
    && cd /tmp/music \
    && proxychains mvn clean package -DskipTests

FROM openjdk:11-jdk-slim

LABEL Author="zhangq"
LABEL Version="2021.08"
LABEL Description="Mpg123 player."

RUN apt update \
    && apt install -y mpg123 ffmpeg \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /music

COPY --from=builder /tmp/music/target/music*.jar app.jar

ENTRYPOINT java -jar -Dfile.encoding=utf-8 app.jar