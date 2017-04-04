#!/bin/sh

curl -XPOST "http://data.duniter.fr/market/comment/_search?pretty" -d'
{
  "query": {
        "bool":{
            "filter": [
                {"term":{
                        "record":"AVbieTIAup9uzWgKipsC"
                    }
                }
            ]
        }
  }
}'

