
IDE (Intellij) VM Option of eureka-server
```text
-javaagent:"/Users/haolun/.m2/repository/org/aspectj/aspectjweaver/1.8.13/aspectjweaver-1.8.13.jar"
```

Run eureka-server (Load time weave)
```bash
java -javaagent:"/Users/haolun/.m2/repository/org/aspectj/aspectjweaver/1.8.13/aspectjweaver-1.8.13.jar" -jar target/*-exec.jar
```

Note:
`src/main/resources/org/aspectj/aop.xml`

File name and location can be specified by system property `-Dorg.aspectj.weaver.loadtime.configuration=org/aspectj/aop.xml`

In eureka-server's application.yml
```yaml
spring:
  aop.auto: false
```
