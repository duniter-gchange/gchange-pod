#!/bin/sh

curl -XPOST "http://data.gchange.fr/market/comment/_search?pretty" -d '
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


curl -XPOST "https://data.gchange.fr/market/record/_search?pretty" -d '
{
    "query": {
        "bool": {
            "filter": [
                {
                    "range": {
                        "stock": {
                            "gt": 0
                        }
                    }
                }
            ]
        }
    },
    "from": 0,
    "size": 100,
    "_source": [
        "category",
        "title",
        "description",
        "stock"
    ]
}'

