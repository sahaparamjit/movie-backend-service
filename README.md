# movie-backend-service


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

