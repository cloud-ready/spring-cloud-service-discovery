package org.springframework.boot.actuate.endpoint;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Endpoint(id = "show-all")
public class ShowAllEndpoint {

  private ApplicationContext applicationContext;

  public ShowAllEndpoint(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @ReadOperation
  public ResponseEntity getEndpoints() {
    final Map<String, Object> endpointBeans = this.applicationContext.getBeansWithAnnotation(Endpoint.class);

    final List<String> endpoints = Lists.newLinkedList();
    for (final Entry<String, Object> entry : endpointBeans.entrySet()) {
      final Object endpointInstance = entry.getValue();
      final Class endpointClazz = endpointInstance.getClass();
      final Endpoint annotation = AnnotationUtils.findAnnotation(endpointClazz, Endpoint.class);
      final String id = "" + AnnotationUtils.getValue(annotation, "id");
      endpoints.add(isNotBlank(id) ? id : "'" + entry.getKey() + "'");
    }
    endpoints.sort(String::compareTo);

    return ResponseEntity.ok().body(endpoints.stream().collect(joining(",\n")));
  }

  @Autowired(required = false)
  public void setObjectMappers(final List<com.fasterxml.jackson.databind.ObjectMapper> objectMappers) {
    objectMappers.forEach(objectMapper ->
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    );
  }
}
