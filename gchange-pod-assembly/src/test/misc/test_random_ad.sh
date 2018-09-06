curl -XGET 'https://data.gchange.fr/market/record/_search?pretty&' -d '{
  "size": 1,
  "query": {
    "bool": {
      "filter": [
        {"range": {"stock": {"gt": 0}}},
        {"term" : {"currency" : "g1"}}
      ],
      "must": {
          "function_score": {
            "query": { "match_all": {} },
            "random_score": {}
          }
      }
    }
  },
  "_source": {
        "exclude": [ "pictures", "category.parent", "category.id", "hash", "signature" ]
    }
}'