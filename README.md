# Github-stats

Simple demo project for recruiting purposes.

# Usage
## REST Api
### GET /users/{login}
Simple endpoint which serves information about user obtained from Github. Additionally adds field calculation, which doesn't make sense in real world - is added just for "do something" with received data. The data came from `https://api.github.com/users/{login}`.

Example response for `/users/slawkow`:
```
{
    "id": "11233089",
    "login": "slawkow",
    "name": "Sławomir Szczurek",
    "type": "User",
    "avatarUrl": "https://avatars.githubusercontent.com/u/11233089?v=4",
    "createdAt": "2015-02-27T17:09:22Z",
    "calculations": "36.0"
}
```

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
