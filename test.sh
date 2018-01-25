#!/bin/bash

curl -H "Content-Type: application/json" -X POST http://localhost:8090/users/init

curl -i -H "Content-Type: application/json" -X POST -d '{
    "username": "admin",
    "password": "password"
}' http://localhost:8090/login


echo "Create customer"
curl -H "Content-Type: application/json" -X POST -d '{
    "locationCode": "123456",
    "haircutAllowed": "true",
    "cacheTDAccount": "999555888",
    "monthlyInterestAllowed": "false"
}'  http://localhost:8090/api/v1/customer/
echo
echo "Create TD"
curl -H "Content-Type: application/json" -X POST -d '{
    "sourceAccount": "888888888",
    "principal": 2500000.00,
    "haircut": 1.5,
    "term": 90
}'  http://localhost:8090/api/v1/customer/123456/td/

echo
echo "Get customer details"
curl http://localhost:8090/api/v1/customer/123456/

