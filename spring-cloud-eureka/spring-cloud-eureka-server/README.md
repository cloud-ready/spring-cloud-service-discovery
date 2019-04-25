#spring-cloud-eureka-server
spring-cloud-eureka-server

![](https://travis-ci.org/cloud-ready/spring-cloud-service-discovery.svg?branch=develop)

Github: https://github.com/cloud-ready/spring-cloud-eureka-server
Docker Hub: https://hub.docker.com/r/cloudready/spring-cloud-eureka-server/


## Run a standalone demo at local

Step 1. Bind hosts  
Edit /etc/hosts, add following content:
```text
127.0.0.1 standalone.eureka
127.0.0.1 eureka-peer1
127.0.0.1 eureka-peer2
127.0.0.1 eureka-peer3
```

Step 2. Create a docker network

<del>`docker network create --driver=bridge --ipv6 --ipam-driver=default --subnet=172.16.238.0/24 --subnet=2001:3984:3989::/64 local-network`</del>

`docker network create --driver=bridge --ipam-driver=default --subnet=172.16.238.0/24 local-network`

Step 3. `docker-compose up` or
```bash
# OSX way to get HOST_IPADDRESS
HOST_IPADDRESS=$(ipconfig getifaddr en0 || ipconfig getifaddr en1) \
docker-compose -f docker-compose-local-cluster.yml up
```

Homepage [http://standalone.eureka:8761](http://standalone.eureka:8761)

Access [http://standalone.eureka:8761/eureka/apps](http://standalone.eureka:8761/eureka/apps) to get application infos.

## Instance Status in Eureka and Overridden Status

Eureka defined 5 status.
+ UP
>Ready to receive traffic
+ DOWN
>Do not send traffic- healthcheck callback failed
+ STARTING
> Just about starting- initializations to be done - do not send traffic
+ OUT_OF_SERVICE
> Intentionally shutdown for traffic
+ UNKNOWN

Overridden Status is for black/red deployment.

1. Set overridden status to `OUT_OF_SERVICE` on part of existing service instances

Take some old instances down:
```bash
curl -i -X PUT http://standalone.eureka:8761/eureka/apps/<application>/<instance-id>/status?value=OUT_OF_SERVICE
```

2. Start up new instances

3. If deployment failed (Found new instances malfunction), take new instances down, and take back old instances.

Delete overridden status
```bash
curl -i -X DELETE http://standalone.eureka:8761/eureka/apps/<application>/<instance-id>/status
```

For example, we have an application named `config-server` which has multiple instances.  
Their instance ids are:

- config-server:config-server:8888
- config-server-2:config-server:9999

To take `config-server:config-server:8888` down, run
```bash
curl -i -X PUT http://standalone.eureka:8761/eureka/apps/config-server/config-server:config-server:8888/status?value=OUT_OF_SERVICE
```

To take `config-server:config-server:8888` back, run
```bash
curl -i -X DELETE http://standalone.eureka:8761/eureka/apps/config-server/config-server:config-server:8888/status
```

You can also set status directly on instances by posting request to its `ServiceRegistryEndpoint`  
URL of `ServiceRegistryEndpoint` is: `http(s)://<host>:<port>[server.servlet.context-path][management.endpoints.web.base-path]/serviceregistry/instance-status`  
`server.servlet.context-path` and `management.endpoints.web.base-path` are optional, that depends on instance's config.  

`/serviceregistry/instance-status` is a sensitive endpoint, may need to authentication (depends on instance's config).   

For example, we can set instance `config-server:8888` of application `config-server` to `OUT_OF_SERVICE` by running:
```bash
curl -i -X POST -u admin:admin_pass -H 'Content-Type: application/json' -d 'OUT_OF_SERVICE' http://config-server:8888/manage/serviceregistry/instance-status
```

Note: only `OUT_OF_SERVICE` and `UP` are acceptable for this endpoint.  

And then we can verify that by
```bash
curl -i -X GET -u admin:admin_pass -H 'Accept: application/json' http://config-server:8888/manage/serviceregistry/instance-status
```
We got `eureka: {overriddenStatus=UNKNOWN, status=UP},consul: OUT_OF_SERVICE`.  
Here may be a bug that eureka's overriddenStatus is not set correctly.  
But this is ok, if you run `curl -i -s -X GET http://standalone.eureka:8761/eureka/apps/config-server/config-server:config-server:8888 | grep overriddenstatus`,
you can see the overriddenStatus on eureka side is updated.  
