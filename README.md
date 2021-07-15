# Example application
to prove https://github.com/spring-cloud/spring-cloud-consul/issues/738

We include a range of properties to show how Spring resolves (or fails to resolve!) the `@Value` annotation.

## Background

| Property | Set in Consul shared | Set in Consul app-specific | Set in application.yml | Should resolve to            | In bug scenario, resolves to
|----------|----------------------|----------------------------|------------------------|------------------------------|------------------------------
| alpha    |          Yes         |             Yes            |           Yes          | alpha-from-consul-specific   |  alpha-from-application-yml
| bravo    |          Yes         |             Yes            |                        | bravo-from-consul-specific   | bravo-default
| charlie  |          Yes         |                            |           Yes          | charlie-from-consul-shared   | charlie-from-application-yml
| delta    |          Yes         |                            |                        | delta-from-consul-shared     | delta-default
| echo     |                      |             Yes            |           Yes          | echo-from-consul-specific    | echo-from-application-yml
| foxtrot  |                      |             Yes            |                        | foxtrot-from-consul-specific | foxtrot-default
| gamma    |                      |                            |           Yes          | gamma-from-application-yml   | gamma-from-application-yml
| hotel    |                      |                            |                        | hotel-default                | hotel-default

The configuration precedence operates as follows:
1. Consul app-specific
2. Consul shared
3. application.yml
4. default value

In the bug scenario, clearly neither (1) nor (2) is considered.

## Reproduce the issue

Clean up old state (necessary if you've run this before at any point)

    docker-compose down
    rm -rf consul/storage

Start up local Consul image (initially WITHOUT ACLs - this is normal process for bootstrapping ACLs on Consul), and
check that it starts up cleanly

    docker-compose up -d consul
    docker-compose logs consul

Set up Consul KVs now, while we don't have any ACLs enabled (just for simplicity)

    docker exec consul consul kv put pfx/shared/alpha   alpha-from-consul-shared
    docker exec consul consul kv put pfx/shared/bravo   bravo-from-consul-shared
    docker exec consul consul kv put pfx/shared/charlie charlie-from-consul-shared
    docker exec consul consul kv put pfx/shared/delta   delta-from-consul-shared

    docker exec consul consul kv put pfx/example-service/alpha    alpha-from-consul-specific
    docker exec consul consul kv put pfx/example-service/bravo    bravo-from-consul-specific
    docker exec consul consul kv put pfx/example-service/echo     echo-from-consul-specific
    docker exec consul consul kv put pfx/example-service/foxtrot  foxtrot-from-consul-specific

Set up ACLs on Consul, and watch the logs

    mv consul/config/consul-acl-config.notyet consul/config/consul-acl-config.json
    docker-compose restart consul
    docker-compose logs -f consul

You can ignore a bunch of `"ACL not found"` errors; but wait until you see messages like the following appear:
```
consul    | 2021-07-22T08:47:51.967Z [INFO]  agent.server: initializing acls
consul    | 2021-07-22T08:47:51.976Z [INFO]  agent.server: Created ACL 'global-management' policy
consul    | 2021-07-22T08:47:51.981Z [INFO]  agent.server: Created ACL anonymous token from configuration
```

Now bootstrap Consul ACLs

    docker exec consul consul acl bootstrap

You should see output similar to the following:
```
AccessorID:       316eb9e2-2c64-6bf4-9e7d-4d7f037ccc0e
SecretID:         612043b5-522f-552b-c361-215ca7c12a6e
Description:      Bootstrap Token (Global Management)
Local:            false
Create Time:      2021-07-22 08:49:38.7938605 +0000 UTC
Policies:
   00000000-0000-0000-0000-000000000001 - global-management
```
Take note of the UUID given by `SecretID`, this is the Consul token you will use henceforth.

Start the application, providing the Consul token

    CONSUL_TOKEN=<your consul token> mvn clean spring-boot:run

The application should start up cleanly, with output including lines similar to the following:
```
...
2021-07-22 08:51:23 INFO  [org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry] (main) Registering service with consul: NewService{id='example-service-b2dc5582cdcbe0cb1880c4da390f1de1-management', name='example-service-management', tags=[management], address='192.168.0.20', meta=null, port=18900, enableTagOverride=null, check=Check{script='null', dockerContainerID='null', shell='null', interval='10s', ttl='null', http='http://192.168.0.20:18900/management/health/liveness', method='null', header={}, tcp='null', timeout='null', deregisterCriticalServiceAfter='null', tlsSkipVerify=null, status='null', grpc='null', grpcUseTLS=null}, checks=null}
...
2021-07-22 08:51:23 INFO  [com.oliverlockwood.example.Application] (main) Started Application in 3.036 seconds (JVM running for 3.438)
2021-07-22 08:51:23 INFO  [com.oliverlockwood.example.Application] (main) Example service is running...
```

While the application is running, in another terminal run:

    curl localhost:18800/api/example

You will see output on both the application logs and the curl response, showing the resolved values for all the properties.

## Demonstrate workaround
1.  Stop the application
2.  Rename `application.yml` to `application.new`
3.  Rename `application.old` and `bootstrap.old` to `application.new` and `bootstrap.new` respectively
4.  Uncomment the `spring-cloud-starter-bootstrap` dependency block in `pom.xml`
5.  Start the application and then hit `curl`, exactly as above
6.  Observe that the KVs are resolved correctly.
