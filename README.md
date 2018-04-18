This guide explains how to build and run the TD server in a dev environment

## Prerequisites
### NPM
Install a recent version of node.js (eg v8), which contains the npm package manager.
Node.js is available [here](https://nodejs.org/en/)

### cURL
curl is a command line HTTP client and can be downloaded [here](https://curl.haxx.se/download.html)

### Create the TD database
Run the script `create-dev.sql` on your local SQL Server instance.

### Create user properties file
The user properties file is where you store the connection string to your personal database.


Create a file named `application-user.properties` in `src\backend\resources`. For example:

```
spring.datasource.url=jdbc:sqlserver://localhost;databaseName=td
spring.datasource.username=tduser
spring.datasource.password=tduser
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
```


### Create a TD admin user

The TD Server has a special URL that, when invoked, will create an admin user in its database.

Start the backend server (see below) and execute the following command:

```
curl -H "Content-Type: application/json" -X POST http://localhost:8090/users/init
```



## Running the TD server in dev mode
### Back End

In IntelliJ, create a run configuration of type Kotlin:
![RunConfiguration](https://kb.greshamtech.com/download/attachments/52725431/kotlin.png?api=v2)

Run this configuration.

### Front End

Go to `src/frontend` and enter `npm start`.

This will start an instance of the webpack dev server on port 8080.
The webpack dev server monitors the `src/frontend` folder, and every time a javascript file is modified it will reload the client in your browser. No need to compile & restart the server with each modification of the frontend.

Point your browser to http://localhost:8080/ and this will show you the TD server homepage.
If webpack doesn't start on port 8080 but 8081 or other, this likely means CTC is still running on your machine. Better stop it and re-run `npm start`.

The dev admin account username is 'admin', password is 'maXDOglj5vZW'.
