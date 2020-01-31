#!/bin/sh

curl -XPOST 'http://192.168.0.20:10988/market/record/_search?pretty' -d '
   {
    "size": 5,
    "_source": ["title", "city"],
     "query": {
       "more_like_this" : {
           "fields" : ["title", "category.id", "type", "city", "issuer"],
           "like" : [
              {
                  "_index" : "market",
                  "_type" : "record",
                  "_id" : "AW_66JbM1PJHS2fVtQvv"
              },
              {
                  "_index" : "market",
                  "_type" : "record",
                  "doc" : {
                    "type": "offer",
                    "category": {
                      "id": "cat19"
                    }
                  }
              }
           ],
           "min_term_freq" : 1,
           "max_query_terms" : 12
       }
    }
   }'