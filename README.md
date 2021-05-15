# Github-stats

Simple demo project for recruiting purposes.

# Installation
### Requirements
- JDK 11+
- Maven
- Docker

## Application

Create project from repository:

```
git clone git@github.com:slawkow/github-stats.git -b master
```

Go to created directory and build project by [maven](http://maven.apache.org/):

```
mvn package
```

Run required docker containers

```
docker compose up -d
```

Run local server with development environment:

```
java -jar -Dspring.profiles.active=local ./target/github-stats-0.1.0.jar
```

After you run local server, project should be available here: [127.0.0.1:8080](http://127.0.0.1:8080)