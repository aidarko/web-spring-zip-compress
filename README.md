# Compress uploaded files 

The project demonstrates compression (into ZIP format) of uploaded files.

- User won't request ZIP file again later

- User looses progress is case of failure

- Process assumes 1 call to endpoint

- HTTP based

- Files fit into RAM


## Build with maven:
```
mvn verify
```
## Run in Docker

Create an image:
```
docker build -t aidar/compress-uploaded-files .
docker run -p 8080:8080 aidar/compress-uploaded-files
```

Endpoint is available on: 
http://localhost:8080/compress