# Wurstgulasch

**Disclaimer**: This is only a proof of concept and entirely work in progress.

Wurstgulasch is a [Camunda BPM] process engine plugin. It provides
process and task discovery based on DNS.

## Table of Contents
* [Idea](#idea)
  * [Scenario](#scenario)
  * [Problem](#problem)
  * [Solution](#solution)
  * [Example](#example)
* [Test It](#test-it)
  * [Build the plugin](#build-the-plugin)
  * [Build the docker image](#build-the-docker-image)
  * [Start the docker images](#start-the-docker-images)
  * [Start some more process engines](#start-some-more-process-engines)
  * [Get the DNS server IP address](#get-the-dns-server-ip-address)
  * [Use DNS server locally](#use-dns-server-locally)
  * [Get current DNS records](#get-current-dns-records)
  * [Run some tests](#run-some-tests)
  * [Run some more tests](#run-some-more-tests)
* [Maintainer](#maintainer)
* [License](#license)


## Idea

### Scenario

A clustered environment with multiple process engines on different hosts.

### Problem

Every process engine provides different resources. Different process definitions,
process instances or tasks. To start new process or complete tasks the correct
process engine has to be called.

### Solution

Every deployed process definition, started process instance or task registers
a DNS entry. To get the responsible process engine the correlating DNS entry
is used.


### Example

There exist three process engines:

- Engine `A` with IP `192.168.0.101`
- Engine `B` with IP `192.168.0.102`
- Engine `C` with IP `192.168.0.103`

In this example the DNS zone is `camunda.example.com`. Every engine will
register a subdomain on start-up:

```
A.camunda.example.com.  300 IN A 192.168.0.101
B.camunda.example.com.  300 IN A 192.168.0.102
C.camunda.example.com.  300 IN A 192.168.0.103
```

Additionally they will all register for the zone entry:

```
camunda.example.com.  300 IN A 192.168.0.101
camunda.example.com.  300 IN A 192.168.0.102
camunda.example.com.  300 IN A 192.168.0.103
```

This allows to use the domain `camunda.example.com` to request a random
process engine installation.

Assume a process definition with the key `invoice` will be deployed on `A` and
`C`. Than both engines will register a DNS record for the `process` subdomain:

```
invoice.process.camunda.example.com. 300 IN A 192.168.0.101
invoice.process.camunda.example.com. 300 IN A 192.168.0.103
```

This allows to use the domain `invoice.process.camunda.example.com` to request a random
process engine on which the process definition with the key `invoice` is deployed.

To start a new instance of the process the REST API on one of the process engines
can be used.

```http
POST /engine-rest/process-definition/key/invoice/start HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 176
Content-Type: application/json
Host: invoice.definition.camunda.example.com:8080
User-Agent: HTTPie/0.9.2

{
    "variables": {
        "amount": {
            "type": "String",
            "value": "$123"
        },
        "creditor": {
            "type": "String",
            "value": "Test"
        },
        "invoiceNumber": {
            "type": "String",
            "value": "IN-VIO-CE"
        }
    }
}

```

The response contains the process instance id of the new process (e.g. `811833d6-0716-11e5-8565-0242ac110049`):

```http
HTTP/1.1 200 OK
Content-Type: application/json
Date: Sat, 30 May 2015 21:54:54 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "businessKey": null,
    "caseInstanceId": null,
    "definitionId": "invoice:1:1535efac-0712-11e5-8565-0242ac110049",
    "ended": false,
    "id": "811833d6-0716-11e5-8565-0242ac110049",
    "links": [
        {
            "href": "http://invoice.definition.camunda.example.com:8080/engine-rest/process-instance/811833d6-0716-11e5-8565-0242ac110049",
            "method": "GET",
            "rel": "self"
        }
    ],
    "suspended": false
}
```

For this process instance id the corresponding process engine registers a DNS entry for the `process` subdomain:

```
811833d6-0716-11e5-8565-0242ac110049.process.camunda.example.com.   300 IN A 192.168.0.103
```

With this domain we can now get the tasks for the process instance:

```http
GET /engine-rest/task?processInstanceId=811833d6-0716-11e5-8565-0242ac110049 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: 811833d6-0716-11e5-8565-0242ac110049.process.camunda.example.com:8080
User-Agent: HTTPie/0.9.2
```

The result will contain the task id (e.g. `811e4e5f-0716-11e5-8565-0242ac110049`).

```http
HTTP/1.1 200 OK
Cache-Control: no-cache
Content-Type: application/json
Date: Sat, 30 May 2015 21:59:59 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

[
    {
        "assignee": "demo",
        "caseDefinitionId": null,
        "caseExecutionId": null,
        "caseInstanceId": null,
        "created": "2015-05-30T21:54:54",
        "delegationState": null,
        "description": "Select the colleague who should approve this invoice.",
        "due": "2015-06-02T21:54:54",
        "executionId": "811833d6-0716-11e5-8565-0242ac110049",
        "followUp": null,
        "formKey": "embedded:app:forms/assign-approver.html",
        "id": "811e4e5f-0716-11e5-8565-0242ac110049",
        "name": "Assign Approver",
        "owner": null,
        "parentTaskId": null,
        "priority": 50,
        "processDefinitionId": "invoice:1:1535efac-0712-11e5-8565-0242ac110049",
        "processInstanceId": "811833d6-0716-11e5-8565-0242ac110049",
        "suspended": false,
        "taskDefinitionKey": "assignApprover"
    }
]
```

The process engine also registers a DNS entry for this task in the `task` subdomain:

```
811e4e5f-0716-11e5-8565-0242ac110049.task.camunda.example.com.  300 IN A 192.168.0.103
```

With this domain the task can be completed:

```http
POST /engine-rest/task/c3762491-0717-11e5-8565-0242ac110049/complete HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 66
Content-Type: application/json
Host: c3762491-0717-11e5-8565-0242ac110049.task.camunda.example.com:8080
User-Agent: HTTPie/0.9.2

{
    "variables": {
        "approver": {
            "type": "String",
            "value": "demo"
        }
    }
}
```


## Test It

### Build the plugin

```bash
mvn -f plugin/pom.xml clean package -DskipTests
```

### Build the docker image

```bash
docker-compose build
```


### Start the docker images

```bash
docker-compose up -d
```

### Start some more process engines

```bash
docker-compose scale camunda=5
```

### Get the DNS server IP address

```bash
docker inspect --format '{{ .NetworkSettings.IPAddress }}' $(docker-compose ps -q bind)
```

### Use DNS server locally

Insert as first `nameserver` line to `/etc/resolv.conf`

```
nameserver BIND_IP
nameserver 192.168.0.1
```


### Get current DNS records

```bash
dig @BIND_IP camunda.example.com AXFR
```

or

```bash
nslookup -query=AXFR camunda.example.com BIND_IP
```

### Run some tests

**Note:** The test client requires python 3 and the module
`requests` to be installed.

```bash
./client/client.py
```

or use docker

```bash
docker-compose -f client/docker-compose.yml up
```

This will create a `invoice` process on a random process
engine and complete two tasks.


### Run some more tests

To run the multiple tests in parallel we use GNU `parallel`.

```bash
time parallel --tag --bar -j 200% ./client/client.py ::: $(seq 100)
```

or use docker

```bash
RUNS=1000 docker-compose -f client/docker-compose.yml up
```


## Maintainer

[Sebastian Menski]


## License

Apache License, Version 2.0


[Camunda BPM]: https://github.com/camunda/camunda-bpm-platform
[Sebastian Menski]: https://github.com/menski
