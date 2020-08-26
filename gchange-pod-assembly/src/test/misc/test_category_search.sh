curl -XGET 'http://localhost:9200/market/category/_search?pretty' -d '{
  "size": 1000
}'