# Nebula
Even though actor-based frameworks such as Akka
have been used by thousands of companies for over a decade, it
is very difficult to configure cloud-based functional actor appli-
cations in a way to minimize their response time and to utilize
provisioned resources with the maximal efficacy at the computing
distributed cloud cluster nodes where these applications are
deployed. As a result, the performance-tuning process is ad-hoc,
laborious, manual, tedious and very expensive.
We developed a novel low-code approach for cross-layer opti-
mization of actor-based distributed applications. In our method,
runtime information is automatically collected and analyzed in
parallel to the executing application using search-based genetic
algorithms. These algorithms generate changes to the applica-
tion design, which are automatically applied to regenerate the
application while it continues to service clients’ requests. We
applied our approach to automatically created distributed Akka
applications demonstrating its effectiveness in finding an optimal
configuration.

## Authors
Andrea Cappelletti  
acappe2@uic.edu 
University of Illinois Chicago

Dr. Mark Grechanik  
drmark@uic.edu 
University of Illinois Chicago

# ISSRE artifact evaluation demo
Please refer to this section for the artifact evaluation demo.
We had to simplify the process to make it easier for the reviewers to evaluate the artifact and use docker containers to run the experiments.

Follow this video for instructions: https://www.loom.com/share/0261298620f7442283a1cbc6048cef93?sid=0b1fefd1-015c-4cfa-bb83-0f69c7423d96

## Environment Setup
Make sure that the following dependencies are installed in your environment.

- Java JDK 8 (1.8.0_331), if you use a different JDK version, please use this [link](https://stackoverflow.com/questions/21964709/how-to-set-or-change-the-default-java-jdk-version-on-macos) to switch. 
- [Scala](https://www.scala-lang.org) 3.2.1 and 2.13.3
- [SBT](https://www.scala-sbt.org) 1.8.0 
- [Python](https://www.python.org) 2.7.16
- [Docker](https://www.docker.com/products/docker-desktop/)

## Getting Started
To run the experiments, you need to follow the steps below.

### Step 1: Run the Cinnamon Telemetry Agent and the GA Optimizer
First, you need to run the Cinnamon Telemetry Agent and the GA Optimizer. 
To do so, you need to start docker on your machine. Then, you can install the GA optimizer by running the following command.

```bash
cd nebula_issre_demo/docker/lambda
docker build -t lambda-fastapi .
``` 

Once the image is built, you can run the entire system by running the following command from the root of the repository.

```bash
cd nebula_issre_demo/docker/telemetry
docker compose up
```

Now you will have the Prometheus Backend Server to collect the run-time metrics information, the Grafana Dashboard to visualize the metrics, and the GA Optimizer to optimize the system.

### Step 2: Reproducibility Instructions
Now you can run the Nebula system. 
To do so, open a new terminal window and run the following command.

```bash
cd nebula_issre_demo
sbt clean compile
sbt run
```

You will be prompted to select the main class
```bash
Multiple main classes detected. Select one to run:
 [1] GUI.runIt
 [2] Jars.LoadExternalJar
```

For demo purposes, select the option 1 to run the GUI.
The configuration of the demo is contained in the `nebula_config` directory.
Please select actors, messages, orchestration, clustering according.


# Artifact Description
The artifact is composed by the following directories:

- `input`: contains the input DSL given to Nebula.
- `nebula_dsl_generator`: contains the project to randomly generate the DSL
- `nebula`: the core of the framework.
- `metrics_collect`: contains a way to store real-time metrics on Firebase.
- `setup_scripts`: contains some scripts to setup the experiments and install the satisfy prerequisites.
- `optimizer`: contains the optimization part with genetic algorithms.
- `spawner`: contains a utility project to spawn the actor systems across multiple nodes.
- `db_output`: contains the database dump of the metrics collected during the experiments.
- `results`: contains the computed final results for the experiments.
- `nebula_issre_demo`: contains the docker containers to run the experiments for the ISSRE artifact evaluation.


The following sections provide a detailed explanation of each part of the project.
For the artifact evaluation, they are not needed to be run.

# Testing environment for the paper evaluation

To perform our experiments, we have been using the following three machines:

| Server                                    | CPU model                                 | # of CPUs | Arch   | RAM          | System Spec                                                                                                     |
|-------------------------------------------|-------------------------------------------|-----------|--------|--------------|-----------------------------------------------------------------------------------------------------------------|
| Los Angeles 10.7.44.49  drm-la.anon.xxx | Intel(R) Xeon(R) CPU E5-2680 v3 @ 2.50GHz | 24        | x86_64 | 164815620 kB | Linux los-angeles 4.4.0-210-generic #242-Ubuntu SMP Fri Apr 16 09:57:56 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux |
| New York  10.7.44.71 drm-ny.anon.xxx    | Intel(R) Xeon(R) CPU E5-2609 0 @ 2.40GHz  | 8         | x86_64 | 98949660 kB  | Linux newyork 4.15.0-163-generic #171-Ubuntu SMP Fri Nov 5 11:55:11 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux     |
| Miami 10.7.45.96  drm-mia.anon.xxx      | Intel(R) Xeon(R) Gold 6148 CPU @ 2.40GHz  | 80        | x86_64 | 131693040 kB | Linux miami 4.15.0-177-generic #186-Ubuntu SMP Thu Apr 14 20:23:07 UTC 2022 x86_64 x86_64 x86_64 GNU/Linux      |


# Installation

In this section, we present how to properly compile and run the code of the repository to reproduce the experiments.

## Nebula DSL Generator

This project allows to generate random DSL configuration based on the specified parameters in the `/src/main/resources/application.conf`. The `graphGenerator` parameter contains the variables to perform the Zer Algorithm: Number of Vertices, Probability and Max. Meanwhile, the `orchestratorGenerator` parameter defines the number of actor instances, the network utilization (our workload), and the time interval for which actors perform operations.

To obtain the random DSL configuration based on the specified parameters, first build this project:

```bash
sbt clean compile
```

After, run the project

```bash
sbt run
```

## Nebula

### Cinnamon Telemetry Agent
In order to properly run Nebula and collect the metrics, you need to setup Lightbend Telemtry.
Since it is a commercial software, you need a license: https://developer.lightbend.com/docs/telemetry/current//setup/setup.html

Before running our Cinnamon Instrumentation we have to install Docker
https://docs.docker.com/get-docker/


Run the dashboard

```bash
cd docker/telemetry
docker-compose up
```
Open a browser and go to the address

```bash
 localhost:3000/
```

### Nebula DSL Configuration
The first specification file contains the declarative language to define composite objects in the system: messages. This file aims to declare the messages that the actors are going to use to exchange information and communicate. 

```json
  [{
    "messageName" : "Init",
    "messageArgs" : []
},{
    "messageName" : "Authentication",
    "messageArgs" : [
      {
        "argName" : "email",
        "argType" : "String",
        "argValue" : "acappe2@uic.edu"},
      {
        "argName" : "password",
        "argType" : "String",
        "argValue" : "mySecretPwd"
}]}]

```
The second specification file contains the declarative language to define actors, more in detail we specify the execution code that can be a function since their entry point is the receive method.

```json
[{"actorName": "FirstActor",
    "actorArgs": [],
    "methods": [{
        "methodName": "receive",
        "methodReturnType": "Receive",
        "caseList": [
          {
            "className": "sayHello",
            "executionCode": "protoMessage",
            "transitions" : ["SecondActor"]}]}]},{
    "actorName": "SecondActor",
    "actorArgs": [],
    "methods": [
      {
        "methodName": "receive",
        "methodReturnType": "Receive",
        "caseList": [
          {"className": "sayHello",
            "executionCode": "protoMessage",
            "transitions" : ["FirstActor"]}]}]}]

```
The third specification file contains the declarative language to define monitoring options to report through Prometheus and Grafana dashboard.

```json

{
  "consoleReporter": false,
  "prometheusHttpServer": true,
  "messageType": true,
  "actors": [
    {
      "path": "Nebula.FirstActor",
      "reporter": "class",
      "thresholds": {
        "mailboxSize": 1000,
        "stashSize": 50,
        "mailboxTime": "3s",
        "processingTime": "500ms"
      }
    }
  ]
}
```
The fourth specification file contains the declarative language to define clustering partition and nodes.

```json
{
  "transport" : "aeron-udp",
  "hostname" : "localhost",
  "port" : 0,
  "seedNodes" : [
    "akka://Nebula@localhost:2551",
    "akka://Nebula@localhost:2561"
  ]
}
```
The fifth specification file contains the declarative language to define the orchestration of the system: how many messages send to init the actors and the number of instances of the actors.

```json
[
  {
    "name" : "FirstActor",
    "initMessages" : ["Authentication"],
    "numOfMessages" : [2, 4],
    "timeInterval" : [10, 20],
    "numOfInstances" : 5
  },
  {
    "name" : "SecondActor",
    "initMessages" : ["sayHello", "computeCost"],
    "numOfMessages" : [2, 4],
    "timeInterval" : [1, 2],
    "numOfInstances" : 5
  }
]
```


### Nebula executable
In order to assembly the fat jar, run this command in the root of the project
```bash
sbt clean assembly
```

You will find the generated jar under `target/scala-3.1.1/` with the name of  `nebula.jar`

To run it type

```bash
java -jar nebula.jar
```

In order to trigger the Cinnamon monitoring instrumentation run this command specifying the -javaagent.
```bash
java -javaagent:/path/to/cinnamon-agent.jar -jar nebula.jar
```

Where `/path/to/cinnamon-agent.jar` is the path to the jar in your machine.

Cinnamon instrumentation gets triggered automatically when using `sbt run` because the -javaagent is implicit into the command.

## Metrics Collector
This project collects real-time metrics and store them into the Firebase Database.

In order to run it, please replace the file `nebula-cf706-firebase-adminsdk-cjswy-56b9e32fa4.json` with your credentials and the database url in the `/src/main/resources/application.conf` under the variable `firebaseDBUrl`.

Then compile the project

```bash
sbt clean compile
```

After, run the project

```bash
sbt run
```

In order to assembly the fat jar, run this command in the root of the project
```bash
sbt clean assembly
```

You will find the generated jar under `target/scala-3.1.1/` with the name of  `metrics_collector.jar`

To run it type

```bash
java -jar metrics_collector.jar
```

## Optimizer
We deployed the Optimizer on AWS through a lambda function so that we can allocate computing resources on demand to speed up the entire reconfiguration process.

The idea behind Pymoo is that we have to define a multi-objective problem to optimise.
In our case, we want to minimise the response time and maximase the amount of used resources. 

The following example provides an idea on how to define our problem in Pymoo using a functional problem definition

```python
import numpy as np
from pymoo.problems.functional import FunctionalProblem
# Objectives definition
objs = [
    lambda x: np.sum((x - 2) ** 2),
    lambda x: np.sum((x + 2) ** 2)
]
# Constraints definition
constr_ieq = [
    lambda x: np.sum((x - 1) ** 2)
]
# Number of variables
n_var = 10
xl = xl=np.array([-10, -5, -10]
xu = np.array([10, 5, 10]
problem = FunctionalProblem(n_var,
                            objs,
                            constr_ieq=constr_ieq,
                            xl=xl),
                            xu=xu])
                            )
```

Make sure that you install all the dependencies specified in the file `requirements.txt`, a fundamental one is `prometheus-api-client` that allows you to fetch the real time metrics from the backend server.

```bash
pip install prometheus-api-client
```

## Spawner
The goal of the spawner is to instanciate an ActorSystem in the nodes of the system, so that they can listen to incoming actors and requests from the other nodes.

In order to run the spawner, you need to create a file named `ip.txt` with a single line that specified the ip address of the node on which you are running it.

Then you can go ahead and compile and run the project

```bash
sbt clean compile
sbt run
```

In order to assembly the fat jar, run this command in the root of the project
```bash
sbt clean assembly
```

```bash
java -jar spawner.jar
```

# Programming technology
All the simulations has been written in Scala using a Functional Programming approach.

While writing the simulations the following best practices has been adopted

- Large usage of logging to understand the system status;


- Configuration libraries and files to provide input values for the simulations;


- No while or for loop is present, instead recursive functions are largely used into the project.


- Modular architecture (code is divided by module to enhance maintainability)



# References
In order to produce the result obtained the following documents and paper
have been consulted.

- https://pymoo.org
- https://github.com/msigwart/fakeload
- https://developer.lightbend.com/docs/telemetry/current//home.html


