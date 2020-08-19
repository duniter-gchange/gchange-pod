curl -XGET 'http://localhost:9200/g1/peer/_search?pretty' -d '{
    "from" : 0,
    "size" : 500,
    "query" : {
      "constant_score" : {
        "filter" : {
          "bool" : {
            "must" : {
              "nested" : {
                "query" : {
                  "bool" : {
                    "filter" : {
                      "term" : {
                        "stats.status" : "UP"
                      }
                    }
                  }
                },
                "path" : "stats"
              }
            }
          }
        }
      }
    }
  }'