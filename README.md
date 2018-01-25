curl -H "Content-Type: application/json" -X POST -d '{
    "firstname": "Bruno",
    "lastname": "Krebs",
    "username": "user",
    "password": "password",
    "DOB": "2017-10-09",
    "gender": "0",
    "phone": "0409928627",
    "email": "675756@gmail.com"
}'  http://localhost:8090/sign-up

curl -H "Content-Type: application/json" -X POST -d '{
    "name":"AAAA", "gender":"1", "ageMin":"0", "ageMax":"100"
}'  http://localhost:8090/ladder




```bash
curl http://localhost:8080/
```

```bash
curl -H "Content-Type: application/json" -X POST -d '{
    "firstName": "Bruno",
    "lastName": "Krebs"
}'  http://localhost:8080/
```


```bash
curl -X DELETE http://localhost:8080/1
```

```bash
curl -H "Content-Type: application/json" -X PUT -d '{
    "id": 6,
    "firstName": "Bruno",
    "lastName": "Simões Krebs"
}'  http://localhost:8080/6
```
