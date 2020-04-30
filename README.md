# Global Kinetic

## Projects
	This folder contains :
		- Angular 9 client app
		- SpringBoot API
		- Video demo

## How to run
- Download and install Docker
- Extract file
- Open up a cmd window
- Navigate to the folder where you extracted the file and type
	- `docker pull ninjanine/globalkapi`
	- `docker pull ninjanine/gloablkclient`
- To start up the api type
	- `docker run -it -p 8081:8081 --rm ninjanine/globalkapi`
- To start up the client type
	- `docker run -it -p 80:80 --rm ninjanine/gloablkclient`
- Navigate in a browser to `http://localhost:80/` to view the app

## Endpoints

- /api/users
    - GET - lists all users
    - POST - creates a user
    - PUT - updates a user
    - DELETE - deletes a user
- /api/users/{login}
    - GET - fetches a single user
- /api/loggedusers
    - GET - fetches active sessions
- /api/login - logs a user in

