FROM openjdk:11.0-jdk

WORKDIR /app
COPY ./build/distributions/GameCfgGen-1.0-SNAPSHOT ./
CMD ["/bin/sh", "bin/GameCfgGen"]


#测试将该进程扔到docker中运行