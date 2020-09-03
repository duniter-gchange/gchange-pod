curl -XGET 'http://localhost:9200/shape/record/_search?pretty' -d '{
  "size": 1,
  "aggs": {
    "countries": {
      "nested" : { "path" : "properties" },
      "aggs": {
         "region": {
           "sum": {
             "field": "id",
             "size": 0
           }
         }
      }
    }
  }
}'