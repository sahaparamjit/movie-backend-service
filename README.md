# movie-backend-service

### Trackig of progress of the project
- [Jira Board](https://learnwithjava.atlassian.net/jira/software/c/projects/SR/boards/1?selectedIssue=SR-2)

### Architecture of the movie service project
![Architecture diagram](/docs/architecture.png)

#### Setup MongoDB in local
```
docker pull mongo
docker run --name mongodb -d -p 27017:27017 mongo
docker logs -f <docker_hash_code_for_container>
```

- Homebrew Mac Setup
```
brew services stop mongodb
brew uninstall mongodb

brew tap mongodb/brew
brew install mongodb-community
```


- How to restart MongoDB in your local machine.
```
brew services restart mongodb-community
```

- Install MongoDB in Windows machine
  https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/

