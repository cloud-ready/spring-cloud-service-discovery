# spring-cloud-eureka-sidecar
spring-cloud-eureka-sidecar

Sidecar can register non-JVM applications or services on Eureka and help them to access applications or services on Eureka.

[Github](https://github.com/cloud-ready/spring-cloud-service-discovery/tree/develop/spring-cloud-eureka)
[Docker Hub](https://hub.docker.com/r/cloudready/spring-cloud-eureka-server/)

## I. Features

### 1. With sidecar non-JVM applications can take advantage of Eureka, Ribbon, and Config Server

Sidecar proxy service calls through an embedded Zuul proxy that gets its route entries from Eureka.
The Spring Cloud Config Server can be accessed directly through host lookup or through the Zuul Proxy.

### 2. The non-JVM application should implement a health check so the Sidecar can report to Eureka whether the app is up or down

## II. Usage

Install [Docker](https://www.docker.com/community-edition) on your host.

```bash
git clone https://github.com/cloud-ready/spring-cloud-service-discovery.git
cd spring-cloud-service-discovery/spring-cloud-eureka/spring-cloud-eureka-sidecar
# export some environments, see following document
docker-compose up
# or run in background
docker-compose up -d
```

### 1. There are 4 mandatory environment variables need to set before sidecar boot-up.

- EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
> Eureka URLs, comma split if there are multiple Eureka servers. default value is 'http://user:user_pass@eureka.local:8761/eureka/'.

- SIDECAR_HEALTHURI
> health check endpoint on no-JVM application (service). default value is '/health'.

The 'SIDECAR_HEALTHURI' is a URI accessible on the non-JVM application that mimics a Spring Boot health indicator.
It should return a JSON document that resembles the following:
```json
{ "status":"UP"}
```
There are 3 possible status 'UP', 'DOWN', 'OUT_OF_SERVICE'.
The simple implementation can just return 'UP' while process is running.
Status 'DOWN' and 'OUT_OF_SERVICE' are needed only when you want to support more advanced routing policy.

- SIDECAR_PORT
> The port on which the non-JVM application listens. default value is '3000'.

Sidecar needs 'SIDECAR_PORT' to register the non-JVM application with Eureka.

- SPRING_APPLICATION_NAME
> Application or service name of the non-JVM application or service 

### 2. Optional environment variables

- SIDECAR_HOMEPAGEURI
> Homepage of the non-JVM application or service
- SIDECAR_HOSTNAME
> Hostname of the non-JVM application or service
- SIDECAR_IPADDRESS
> Ip address of the non-JVM application or service

Optional SIDECAR_* variables are needed only when sidecar can not properly register the application with default values 
and there are issues on service access.

- SERVER_PORT
> The port sidcar listens. Access services on Eureka through this port. default value is '5678'.

The API for the `DiscoveryClient.getInstances()` method is `/hosts/{serviceId}`
`http://localhost:5678/hosts/{serviceId}` if the sidecar is on port 5678.


The Zuul proxy automatically adds routes for each service known in Eureka to /{serviceId}, 
so the customers service is available at /customers. 
The non-JVM application can access the customer service at `http://localhost:5678/customers` (assuming the sidecar is listening on port 5678).


## III. 

Run this sidecar on the same host as the non-JVM application.
If sidecar not on the same host as the non-JVM application, you need to set environment variables.

- SIDECAR_HEALTHURI
- SIDECAR_HOMEPAGEURI
- SIDECAR_HOSTNAME
- SIDECAR_IPADDRESS