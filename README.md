# Verve
REST service to get count of unique requests

Sample Java API (GET requests on the `/api/verve/accept`) to get count of unique requests your application received in that minute to a log file.

## To build java file:
- first compile java code
- Navigate to src folder then trigger : javac -d . com/java/restapi/SampleApi.java
- Once code compiled successfuly
- Run java code : java com.java.restapi.SampleApi
- Server is running on port 8080

## Sample request:
 - http://localhost:8080/api/verve/accept?id=1 
 - Endpoint: `/api/verve/accept/{id"}`
 - Response Messages:
  - "ok" if the request is processed successfully.
  - "failed" if there are errors (e.g. missing id parameter or invalid id).
