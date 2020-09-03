curl -XGET 'http://localhost:9200/shape/record/_search?pretty' -d '{
  "size": 0,
  "aggs": {
    "countries": {
      "nested" : { "path" : "properties" },
      "aggs": {
        "names": {
          "terms": {
              "field": "properties.country",
              "size": 0
          },
          "aggs" : {
             "nb_regions" : { "value_count" : { "field" : "properties.id" } }
          }
        }
      }
    }
  }
}'