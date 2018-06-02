
FROM cirepo/java:oracle-8u171-jdk-alpine

VOLUME /tmp
VOLUME ["/root/.data"]











ADD src/main/docker/entrypoint.sh /root/
RUN chmod 755 /root/*.sh
ARG JAR_FILE
ADD	target/${JAR_FILE} /root/
RUN touch /root/*-exec.jar

EXPOSE 8761
USER root
WORKDIR /root/
ENTRYPOINT ["/root/entrypoint.sh"]
CMD []
