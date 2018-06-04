package org.springframework.boot.actuate.endpoint;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ListEndPoints extends AbstractEndpoint<List<Endpoint>> {

  private List<Endpoint> endpoints;

  public ListEndPoints(final List<Endpoint> endpoints) {
    super("showendpoints");
    this.endpoints = endpoints;
  }

  @Override
  public List<Endpoint> invoke() {
    return this.endpoints;
  }

  @Autowired(required = false)
  public void setObjectMappers(final List<com.fasterxml.jackson.databind.ObjectMapper> objectMappers) {
    objectMappers.forEach(objectMapper ->
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    );
  }
}
