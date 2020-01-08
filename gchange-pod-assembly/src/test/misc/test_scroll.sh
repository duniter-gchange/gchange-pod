#!/bin/sh

curl -XPOST 'https://data.gchange.fr/market/record/_search?pretty&scroll=10s' -d '
   {
    "size": 10,
    "_source": ["title"],
    "sort": ["_doc"],
     "query":{
          "constant_score" : {

            "filter" : {
              "range" : {
                "time" : {
                  "from" : 0,
                  "to" : null,
                  "include_lower" : true,
                  "include_upper" : true
                }
              }
            }
          }
     }
   }'

#curl -XPOST 'http://localhost:9200/_search?scroll=1m -d 'cXVlcnlUaGVuRmV0Y2g7MzsyNTY0OTE6WUkyT25rYy1RUWFxRDFDYmNjUzlHUTsyNTY0OTA6WUkyT25rYy1RUWFxRDFDYmNjUzlHUTsyNTY0OTI6WUkyT25rYy1RUWFxRDFDYmNjUzlHUTswOw==';'

#curl -XPOST 'https://data.gchange.fr/market/record/_search?pretty&scroll=1m' -d '
#{"query":{"bool":{"should":{"range":{"time":{"from":0,"to":null,"include_lower":true,"include_upper":true}}}}},"size":250, "sort": ["_doc"]}'

