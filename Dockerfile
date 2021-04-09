FROM openjdk:8-jdk-alpine
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/ganache
ADD build/libs /usr/local/ganache/libs
RUN chmod a+rx -R /usr/local/ganache/libs
WORKDIR /usr/local/ganache/libs
CMD ./run.sh