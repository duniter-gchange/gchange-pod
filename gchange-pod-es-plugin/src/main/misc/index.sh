#!/bin/sh

curl -X POST '192.168.0.28:9200/places' -d '{
    "mappings": {
    "place": {
        "properties": {
            "id": {"type": "double"},
            "name": {"type": "string"},
            "type": {"type": "string"},
            "location": {"type": "geo_point"}
        }
    }
  }
}'