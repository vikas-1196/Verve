# Verve
REST service to get count of unique requests

Sample Java API (GET requests on the `/api/verve/accept`) to get count of unique requests your application received in that minute to a log file.

## To build java file:
- first compile java code
- javac -d out src/com/java/restapi/SampleApi.java
- Once code compiled successfuly
- Command to create jar file : jar cfm SampleApi.jar MANIFEST.MF -C out/ .
- This command create SampleApi.jar file
- To run jar file: java -jar SampleApi.jar
- Server is running on port 8080

## Added sample jar file
 - Run jar file : java -jar SampleApi.jar
 - Trigger API to test

## Sample request:
 - http://localhost:8080/api/verve/accept?id=1 
 - Endpoint: `/api/verve/accept/{id"}`
 - Response Messages:
  - "ok" if the request is processed successfully.
  - "failed" if there are errors (e.g. missing id parameter or invalid id).
