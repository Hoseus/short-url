# short-url

This project uses two of my libraries:
- [lib-logging-quarkus](https://github.com/Hoseus/lib-logging)
- [lib-error](https://github.com/Hoseus/lib-error)

### API docs:

https://app.swaggerhub.com/apis/Hoseus/short-url/1.0.0

### Native build:

``` shell
./gradlew clean build -Dquarkus.package.type=native-sources
cd build/native-sources
docker run -u "$(id -u):$(id -g)" -it --rm -v $(pwd):/work -w /work --entrypoint /bin/sh quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.3-java17 -c "native-image $(cat native-image.args) -J-Xmx4g"
```

### Dependencies:

For this to work mysql, redis and cloud config server are needed.

cloud config server properties to define here.
```yaml
quarkus:
  spring-cloud-config:
    enabled: true
    url: "http://localhost:8888"
    label: main
    fail-fast: true
```

mysql properties to define in the remote config repository.
```yaml
quarkus:
  datasource:
    db-kind: mysql
    username: "<username>"
    password: "<password>"
    jdbc:
      url: jdbc:mysql://<host>:<port>/<db>
```

redis properties to define in the remote config repository.
```yaml
quarkus:
  redis:
    hosts: "redis://<host>:<port>"
```

### Release and versioning:

I used [uplift](https://upliftci.dev/)

#### 1) Release
```shell
uplift release
```
