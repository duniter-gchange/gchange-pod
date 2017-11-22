
curl -XPOST 'http://localhost:9200/market/record/_search?pretty' -d '
   {
      "size": 10,
      "filter" : {
            "geo_distance" : {
                "distance" : "20km",
                "geoPoint" : {
                    "lat" : 48.1937599,
                    "lon": -0.664589
                }
            }
        }
   }'