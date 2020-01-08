curl -XGET 'http://localhost:9200/like/record/_search?pretty' -d '{
  "query": {
    "bool": {
      "filter": [
        {"term" : {"id" : "AW8Q5RFa6_tlAKMUKabg"}}
      ],
      "should":
        {"term" : {"issuer" : "BxGxCA8UrM6HURr8kTiq7gQVwsZtXekG7dmboUGFNG1L"}}

    }
  },
  "size": 1,
  "_source": [ "issuer" ]
}'