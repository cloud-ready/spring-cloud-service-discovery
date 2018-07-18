
FROM cirepo/java-oracle:8u171-en_US.UTF-8_Etc.UTC-alpine


ARG JAR_FILE


COPY --chown=alpine:alpine src/main/docker /
COPY --chown=alpine:alpine target/${JAR_FILE} /home/alpine
RUN set -ex \
  && chmod 0755 /home/alpine/*.sh
