curl -XGET 'http://localhost:9200/g1/movement/_search?pretty' -d '{
  "query": {
    "bool": {
      "filter": [
        {"term" : {"recipient" : "38MEAZN68Pz1DTvT3tqgxx4yQP6snJCQhPqEFxbDk4aE"}}
      ],
      "must": {
        "prefix": {
           comment: "GCHANGE:AXQGHDS5SE1YLAUAFL0W"
        }
      }
    }
  },
  "size": 0,
  "aggs": {
    "amount_sum": {
      "sum" : { "field" : "amount" }
    },
    "amount_avg": {
      "avg" : { "field" : "amount" }
    },
    "issuers": {
      "terms": { "field": "issuer" }
    }
  }
}'