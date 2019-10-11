echo "====CLEAR OLD DATA===="
echo "curl -s -X DELETE http://localhost:4567"
curl -s -X DELETE http://localhost:4567 | jq
echo

echo "====PUT TWO ITEMS===="
read -p "curl -X PUT http://localhost:4567 -d '{\"title\":\"Buy milk\", \"completed\": false}'"
milkJson="$(curl -s -X PUT http://localhost:4567 -d '{"title":"Buy milk", "completed": false}' | jq)"
echo ${milkJson} | jq
read -p "curl -X PUT http://localhost:4567 -d '{\"title\":\"Buy apples\", \"completed\": true}'"
applesJson="$(curl -s -X PUT http://localhost:4567 -d '{"title":"Buy apples", "completed": true}' | jq)"
echo ${applesJson} | jq
milkId="$(echo ${milkJson} | jq -r '.id')"
applesId="$(echo ${applesJson} | jq -r '.id')"
echo

echo "====GET FULL LIST===="
read -p "curl http://localhost:4567/"
curl -s http://localhost:4567/ | jq
echo

echo "====GET COMPLETED LIST===="
read -p "curl http://localhost:4567/?q=completed"
curl -s http://localhost:4567/?q=completed | jq
echo

echo "====GET SINGLE ID===="
read -p "curl http://localhost:4567/todo/${milkId}"
curl -s http://localhost:4567/todo/${milkId} | jq
echo

echo "====PATCH 'Buy milk' TO COMPLETED===="
read -p "curl -X PATCH http://localhost:4567/todo/${milkId} -d '{\"id\":\"$milkId\",\"completed\": true}'"
curl -s -X PATCH http://localhost:4567/todo/${milkId} -d '{"id":"$milkId","completed":"true"}' | jq
echo

echo "====GET FULL LIST===="
read -p "curl http://localhost:4567/"
curl -s http://localhost:4567/ | jq
echo

echo "====DELETE 'Buy milk'===="
read -p "curl -X DELETE http://localhost:4567/todo/${milkId}"
curl -s -X DELETE http://localhost:4567/todo/${milkId} | jq
echo

echo "====DELETE 'Buy apples'===="
read -p "curl -X DELETE http://localhost:4567/todo/${applesId}"
curl -s -X DELETE http://localhost:4567/todo/${applesId} | jq
echo
