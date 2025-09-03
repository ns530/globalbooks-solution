#!/bin/bash

# Test Create Order
echo "Testing Create Order..."
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "Jane Smith", "items": [{"bookId": 1, "bookTitle": "Java Programming", "quantity": 2, "price": 29.99}], "totalPrice": 59.98}'

echo -e "\n"

# Test Get Order by ID
echo "Testing Get Order by ID..."
curl -X GET http://localhost:8080/orders/1

echo -e "\n"