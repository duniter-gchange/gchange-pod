curl -XGET 'http://localhost:9200/like/record/_search?pretty' -d '{
  "query": {
    "bool": {
      "filter": [
        {"term" : {"id" : "HTYsdtCzjtdc2hUdN985Apih2Y5tnvPxkaRE7fRYc5Eh"}}
      ],
      "should":
        {"term" : {"issuer" : "G2CBgZBPLe6FSFUgpx2Jf1Aqsgta6iib3vmDRA1yLiqU"}}

    }
  },
  "size": 1,
  "aggs": {
    "level_sum": {
      "sum" : { "field" : "level" }
    }
  }
}'