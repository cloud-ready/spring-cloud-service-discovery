# docker-consul-agent
docker-consul-agent

## Running Consul for Development

In-memory Consul server agent with default bridge networking and no services exposed on the host
For development, should not be used in production
```yaml
command: ["agent", "-dev", "-client", "0.0.0.0"]
```

Join the existing node
```yaml
command: ["agent", "-dev", "-join=<root agent ip>"]
```

## Running Consul Agent in Client Mode
This runs a Consul client agent sharing the host's network and advertising the external IP address to the rest of the cluster.
Note that the agent defaults to binding its client interfaces to 127.0.0.1, which is the host's loopback interface. 
This would be a good configuration to use if other containers on the host also use --net=host, 
and it also exposes the agent to processes running directly on the host outside a container, such as HashiCorp's Nomad.
```yaml
command: ["agent", "-bind=<external ip>", "-retry-join=<root agent ip>"]
```

If you want to expose the Consul interfaces to other containers via a different network, such as the bridge network, use the -client option for Consul:
```yaml
command: ["agent", "-bind=<external ip>", "-client=<bridge ip>", "-retry-join=<root agent ip>"]
```

## Running Consul Agent in Server Mode
This runs a Consul server agent sharing the host's network. 
All of the network considerations and behavior we covered above for the client agent also apply to the server agent. 
A single server on its own won't be able to form a quorum and will be waiting for other servers to join.
```yaml
command: ["agent", "-server", "-bind=<external ip>", "-retry-join=<root agent ip>", "-bootstrap-expect=<number of server agents>"]
```

## Exposing Consul's DNS Server on Port 53
By default, Consul's DNS server is exposed on port 8600. 
Because this is cumbersome to configure with facilities like resolv.conf, you may want to expose DNS on port 53. 
Consul 0.7 and later supports this by setting an environment variable that runs setcap on the Consul binary, allowing it to bind to privileged ports. 
Note that not all Docker storage backends support this feature (notably AUFS).

```yaml
command: ["agent", "-dns-port=53", "-recursor=8.8.8.8", "-bind=<bridge ip>"]
```

Test DNS
```bash
docker run -i --dns=<bridge ip> -t ubuntu sh -c "apt-get update && apt-get install -y dnsutils && dig consul.service.consul"
```


see: https://github.com/hashicorp/docker-consul

see: https://hub.docker.com/r/library/consul/

see: https://www.consul.io/docs/agent/basics.html
