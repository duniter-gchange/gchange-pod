
# HTTP API

## Contents

- [Contents](#contents)
- [Overview](#overview)
- [GCHANGE API](#GCHANGE) Data on the G1 blockchain
   * [node](#node)
      * [node/summary](#nodesummary)
      * [node/stats](#nodestats)
      * [node/moderators](#nodemoderators)
   * [currency](#acurrency)
      * [currency/block](#currencyblock)
      * [currency/blockstat](#currencyblockstat)
      * [currency/peer](#currencypeer)
      * [currency/movement](#currencymovement)
      * [currency/pending](#currencypending)
   * [user](#user)
      * [user/event](#userevent)
      * [user/profile](#userprofile)
      * [user/settings](#usersettings)
   * [message](#message)
      * [message/inbox](#messageinbox)
      * [message/oubox](#messageoutbox)
   * [invitation](#invitation)
      * [invitation/certification](#invitationcertification)
   * [subscription](#subscription)
      * [subscription/record](#subscriptionrecord)
   * [page](#page)
      * [page/record](#pagerecord)
      * [page/comment](#pagecomment)
   * [market](#market)
      * [market/record](#marketrecord)
      * [market/comment](#marketcomment)
  
## Overview

Gchange Pod offer RESTfull HTTP access:

- BlockChain indexation and statistics;
- User data indexation, such as: profiles, private messages, encrypted settings;
- User service subscription, such as: email notification service;
- Ads, such as: offer, need, auction and crowdfunding;

Data is made accessible through an HTTP API :

```text
    http[s]://node[:port]/...
    |-- <currency>/
    |   |-- block
    |   |-- blockstat
    |   |-- peer
    |   |-- movement
    |   `-- pending
    |-- document/
    |   `-- stats
    |-- message/
    |   |-- inbox
    |   `-- outbox
    |-- invitation/
    |   `-- certification
    `-- subscription/
    |   `-- record
    |-- page/
    |   |-- record
    |   `-- comment
    `-- market/
        |-- record
        `-- comment
```

### Document format
 
All stored documents use a JSON format.

#### Data document

Every document have the following mandatory fields:

- `version` : The document's version.
- `issuer` : The document's emitter
- `hash`: the document's hash
- `signature`: the signature emitted by the issuer. Since `version: 2`, only the `hash` is signed.

#### Deletion

Document deletion use a document with this mandatory fields:

- `index` : The document's index
- `type` : The document's type
- `issuer`: The deletion issuer. Should correspond to the document's `issuer`, or the `recipient` in some special case ([inbox message](#messageinbox) or [invitation](#invitation))
- `time`: the current time
- `hash`
- `signature`

For instance, a deletion on `message/inbox` should send this document:

```json
{
  "version" : 2,
  "index" : "message",
  "type" : "inbox",
  "id" : "AV9VOeOuTvXJwYisNfU6",
  "issuer" : "F13aXKWQPGCjSQAxxTyJYyRyPm5SqzFSsYYWSDEQGi2A",
  "time" : 1509806623,
  "hash" : "61EBBFBCA630E8B715C360DDE1CD6CABD92B9267CA4B724A2F1F36F0FF7E3455",
  "signature" : "FOkYCX1b05LTAbtz72F/LMWZb8F8zhQKEqcvbuiQy1N6AXtCUC5Xmjcn+NeO9sCLdcmA0HxsJx42GnWZOmKCDA=="
}
```
          
## GCHANGE_API

### `node/*`

#### `node/summary`

 - Get software version of the pod (using the format defined by Duniter BMA API);

    ```json
    {
      "duniter" : {
        "software" : "cesium-plus-pod",
        "version" : "1.6.1",
        "status" : 200
      }
    }
    ```

#### `node/stats`

 - Get statistics on the pod:
   * Number of listeners open by WebSocket sessions;
   * Status of the embedded ES cluster ; 
   * etc.  

    ```json
    { 
       "stats" : {
            "listeners" : [ {
              "source" : "subscription/execution,record",
              "count" : 1
            }, {
              "source" : "user/event",
              "count" : 1
            }, {
              "source" : "group/comment",
              "count" : 1
            },{
              "source" : "page/comment",
              "count" : 1
            }, {
              "source" : "*/block",
              "count" : 2
            }, {
              "source" : "g1/block/current",
              "count" : 1
            } ],
            "cluster" : {
              "timestamp" : 1583310606941,
              "cluster_name" : "g1-es-data",
              "status" : "yellow",
              "indices" : {
                 "count" : 12             
             }
           }
        }
    }
    ```
#### `node/moderators`

 - Get pubkeys of node moderators. Moderators can delete document (profiles, etc.) on pod;

    ```json
    {
      "moderators": [
        "38MEAZN68Pz1DTvT3tqgxx4yQP6snJCQhPqEFxbDk4aE",
        "47JpfrGkoHJWtumeu7f67fbAxkvaHYVQBNo5GszNs61Z",
        "HmH5beJqKGMeotcQUrSW7Wo5tKvAksHmfYXfiSQ9EbWz"
      ]
    }
    ```

### currency/*

#### currency/block

 - Get the current block: `<currency>/block/current`
 - Get a block by number: `<currency>/block/<number>`
 - Search on blocks (using the ElasticSearch search API): `<currency>/block/_search` (POST or GET)

#### `<currency>/blockstat`

 - Search on blocks, with count of each parts of a block: `certCount`, `memberCount`, `txCount`, `txAmount`, etc.
   (using the ElasticSearch search API): `<currency>/blockstat/_search` (POST or GET)

   ```json
    {
      "took" : 3,
      "timed_out" : false,
      "_shards" : {
        "total" : 3,
        "successful" : 3,
        "failed" : 0
      },
      "hits" : {
        "total" : 302494,
        "max_score" : 1.0,
        "hits" : [ {
          "_index" : "g1",
          "_type" : "blockstat",
          "_id" : "29003",
          "_score" : 1.0,
          "_source" : {
            "version" : 10,
            "currency" : "g1",
            "number" : 29003,
            "issuer" : "H1yTj77m946f52u64FvR36o9SmD38Ye2j1H4XCvwBFXK",
            "hash" : "0000024FD141E2DA5E3BCAC15CD558EF47BD9CB38DE022F2E0E8727AA885FCD4",
            "medianTime" : 1498016342,
            "membersCount" : 161,
            "monetaryMass" : 10533000,
            "unitbase" : 0,
            "dividend" : null,
            "txCount" : 1,
            "txAmount" : 2500,
            "txChangeCount" : 0,
            "certCount" : 0
          }
        }
     }
   }
   ```
   
#### `<currency>/peer`

 - Search on peers by endpoint and API, with a status (UP, DOWN) and other stats on blockchain (main consensus, ...)
 
   (using the ElasticSearch search API): `<currency>/peer/_search` (POST or GET)

    ```json
    {
      "took" : 7,
      "timed_out" : false,
      "_shards" : {
        "total" : 3,
        "successful" : 3,
        "failed" : 0
      },
      "hits" : {
        "total" : 691,
        "max_score" : 1.0,
        "hits" : [ {
          "_index" : "g1",
          "_type" : "peer",
          "_id" : "54F93B092CAA3A4B287F39517C7B446A2D010BE4DD0E2FDBC76E0920B3714532",
          "_score" : 1.0,
          "_source" : {
            "api" : "BASIC_MERKLED_API",
            "dns" : null,
            "ipv4" : "91.160.74.131",
            "ipv6" : "2a01:e0a:12:a0c0:4951:28fb:745a:9ec9",
            "epId" : null,
            "pubkey" : "8dyDUH4KCMgpHkzAS9uu4NWPSf2xBmrp6D6S7d37A8M7",
            "hash" : "54F93B092CAA3A4B287F39517C7B446A2D010BE4DD0E2FDBC76E0920B3714532",
            "currency" : "g1",
            "stats" : {
              "version" : "1.5.9",
              "status" : "DOWN",
              "blockNumber" : 76834,
              "blockHash" : "000005D2162A8ECDC3A1268FC76D1F42AC3DEAE0479640D56D877796EB6F4968",
              "error" : null,
              "medianTime" : 1512998820,
              "hardshipLevel" : null,
              "consensusPct" : 77.77777777777777,
              "uid" : null,
              "lastUpTime" : 1513002789,
              "mainConsensus" : true,
              "forkConsensus" : false,
              "firstDownTime" : 1564882432
            },
            "port" : 10901,
            "useSsl" : false
          }
        }
     }
   }
   ```
 
#### `<currency>/movement`

 - Allow to search on all blockchain transactions, but split by issuer.
 
   (using the ElasticSearch search API): `<currency>/movement/_search` (POST or GET):
 
    ```json
    {
     "took" : 7,
     "timed_out" : false,
     "_shards" : {
       "total" : 3,
       "successful" : 3,
       "failed" : 0
     },
     "hits" : {
       "total" : 67237,
       "max_score" : 1.0,
       "hits" : [ {
         "_index" : "g1",
         "_type" : "movement",
         "_id" : "AWAsh08ZeZsmCDV2GYit",
         "_score" : 1.0,
         "_source" : {
           "currency" : "g1",
           "medianTime" : 1498016342,
           "version" : 10,
           "issuer" : "TENGx7WtzFsTXwnbrPEvb6odX2WnqYcnnrjiiLvp1mS",
           "recipient" : "D9D2zaJoWYWveii1JRYLVK3J4Z7ZH3QczoKrnQeiM6mx",
           "amount" : 320,
           "unitbase" : 0,
           "comment" : "REMU:28501:29000",
           "reference" : {
             "index" : "g1",
             "type" : "block",
             "id" : "29003",
             "anchor" : null,
             "hash" : "0000024FD141E2DA5E3BCAC15CD558EF47BD9CB38DE022F2E0E8727AA885FCD4"
           },
           "ud" : false
         }
       }
       }
    ```


#### `<currency>/pending`

 - All pending membership, collected from Duniter peers (selected randomly, each hour);

   * Allow to search on pending memberships (including old memberships), using `<currency>/pending/_search` (using ES search API);
 
   * Output as Duniter BMA format, using `<currency>/pending`:
 
    ```json
    {
      "memberships" : [ {
        "pubkey" : "C59xaJfBXLpn9YhKet64KS3geTV9UQjvkLqpcTDy6dcK",
        "uid" : "JacquesDupond",
        "version" : "10",
        "currency" : "g1",
        "membership" : "IN",
        "blockNumber" : 302423,
        "blockHash" : "0000038FD71F131B76FF76F3E1D0CBA08901DD350551867CE5611DDAA1C1E057",
        "written" : null
      }, {
        "pubkey" : "GhicsNYYPrMvp1inmgxKUjww1s9PLnuzSV8CGtHrjUbq",
        "uid" : "MikeHorn",
        "version" : "10",
        "currency" : "g1",
        "membership" : "IN",
        "blockNumber" : 302376,
        "blockHash" : "000000F466B3BE9E93B1C19FF7A6A5556D9CD750D84458B660FEA1B81BA406B2",
        "written" : null
     }]
   }
    ```

### `user/*`

#### `user/event`

 - Get events on an account, by pubkey: `user/event/_search?q=issuer:<pubkey>` (GET)
 - Search on events: `user/event/_search` (POST or GET)

#### `user/profile`


 - Get an profile, by public key: `user/profile/<pubkey>`
 - Add a new profile: `user/profile` (POST)
 - Update an existing profile: `user/profile/_update` (POST)
 - Delete an existing invitation: `invitation/certification/_delete` (POST)
 - Search on profiles: `user/profile/_search` (POST or GET)

A profile document is a JSON document. Mandatory fields are:
 
 - `title`: user name (Lastanem, firstname...)
 - `time`: submission time, in seconds
 - `issuer`: user public key
 - `hash`: hash of the JSON document (without fields `hash` and `signature`)
 - `signature`: signature of the JSON document (without fields `hash` and `signature`)

Example with only mandatory fields:

```json
{
    "version" : 2, 
    "title" : "Pecquot Ludovic",
    "description" : "Développeur Java et techno client-serveur\nParticipation aux #RML7, #EIS et #Sou",
    "time" : 1488359903,
    "issuer" : "2v6tXNxGC1BWaJtUFyPJ1wJ8rbz9v1ZVU1E1LEV2v4ss",
    "hash" : "F66D43ECD4D38785F424ADB68B3EA13DD56DABDE275BBE780E81E8D4E1D0C5FA",
    "signature" : "3CWxdLtyY8dky97RZBFLfP6axnfW8KUmhlkiaXC7BN98yg6xE9CkijRBGmuyrx3llPx5HeoGLG99DyvVIKZuCg=="
}
```

Some additional fields are `description`, `socials`, `tags` and `avatar` :

```json
{
    "version" : 2, 
    "title" : "My profile name",
    "description" : "#developer",
    "city" : "Rennes",
    "socials" : [ {
      "type" : "diaspora",
      "url" : "https://diaspora-fr.org/people/f9d13420f9ssqzq97aa01beea1f31e2"
    } ],
    "time" : 1487422234,
    "tags" : [ "developer" ],
    "issuer" : "2ny7YAdmzReQxAayyJZsyVYwYhVyax2thKcGknmQy5nQ",
    "avatar" : {
      "_content_type" : "image/png",
      "_content" : "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkC(...)" // base 64 encoding
    }
    "hash" : "85F527077D060E03ECAC6D1AE38A74CCC900ACAF5D52F194BA34F5A5E8A55139",
    "signature" : "WeP7JEwttAoSkHcuiFwo6N4SM0uVakTYBQ09H1+K8/nPFyxO3ak1U9EQ6qaQFoAx9IdDp5qO2EX662wP/pcEAg==",
}
```

#### `user/settings`

 - Get an settings, by wallet pubkey: `user/settings/<pubkey>`
 - Add a new settings: `user/settings` (POST)
 - Delete an existing settings: `user/settings/_delete` (POST)
 - Search on settings: `user/settings/_search` (POST or GET)

Some additional fields are `content` (the settings content, but encrypted) and `nonce` (required for an optimal encryption security level) :

```json
{
    "hash" : "58ECD135719628AA6DCE6DEFE1C2B328B04047B836BC478D0CF9E6F5A515896EC",
    "signature" : "3zP/mOgwnTj6EAfhb9vNfSUoPLZLqMwTP9QDk4wShTXlWnFPmPl2To3VTAoS3aHbLQAKDAWZa6EeVfsYCDVoDg==",
    "issuer" : "EtmXYFdh6WjgKyX3D6s2fXphCDv7jRPRnqnkKFTdMwNr",
    "nonce" : "Lfv5wXbLKF3RY9qbQVgv914ZKbsVi1sAm",
    "time" : 1516311640,
    "content" : "4bW4cL075bLWuTrHRuo69P0glmZJiVKF/AOtRt1e3trcm+Es/E77cYdAL00TCQw8N1kVU6fznCmZyVxtD8gfxpZwcoipWjWeTZTu21SxtPDxTxEvAV4gxbmOk/Li9oMy04WOmpkbsKawmdYW2oaKzz3psJXn4C4/jFQZIL/X863R9sQDGWPHm8MRvCaP7xQT+MMSpb8/1lIgf5443PKBixQbcY4fcqDRK3365xG2jDZEJ/uVZ/bRPJyjclKgBEd8xariJUV+zdh31f/qHhnQlcg/kLmdQ4sja2L/BWE5kTFlajRqOJDGrtuRafWTFamoUKZDE8C9YeivvFR7oGwY0zPE0uFnuZCGAvm3xC13ekpsqDv9YtBmZhou7AZAtw9JV81QuHoorWrka7C3LW12YuOSBKxkZNCi0tPHmF2ArI5WJl7W",
    "version" : 2
}
```


### `message/*`

#### `message/inbox`

Some additional fields are `recipient` (the message recipient), `title` (the encrypted message's title), `content` (the encrypted message's body) and `nonce` (required for an optimal encryption security level) :

```json
{
    "issuer" : "DMwEdBiWuCGkPvtfvGs7fAoyaqbiA3ZXeX5grcNsg5x8",
    "recipient" : "FbvRnCM8gQdDig614qR1y1QY7x7sUN2RzXr3a9D9Rzw4",
    "title" : "jBhFV2kDyUvDxmYtodsO1D9ZpbX+j2vyOjVuMkgWa4vTyvM6VCqZWhutwYpMmXr1vdA=",
    "content" : "sX5/XOwcZe2RUIM2jd7Wlz9NoLTvgxPbNXZtNU8Qa4vT2qApB2rHFloNFdk+mHHo+NOfwk6RZjU3gnmUN0yi3eyUIOr2FYAoltdhx6C4Zmd9JQy5jVMzKg1HfD6K7daYbtN72ZTLfIxwlnAXG8z1+Rf9hZcmRNSIpFJ6lC2IUFcrFbulE8EsZuMPtrfuLlzNoW8HButmBlfbkMlALKcOcGYhVUCFhVAiL5FSgF+sHsUZe9CtubeGPlNT9m1y2joNZ8B4/rBn97XGV5odsaZZBO5gqcRQN4Y9SJSaNEbNFdaeWIFaO4NPT0r48eXKP4OGhYeQl4vCUQGG21U+GmcJiT4hiYJm41Xwp+qyjePlJ+om",
    "time" : 1504199349,
    "nonce" : "CWKAtBXqXu2ZuifeHnRBePw15e36gw3v9",
    "hash" : "965E9C4693C0B63C6F4CC6924A93354F0CE2B16F91BF0243FB5A355B4222D502",
    "signature" : "f0sPHFKukSbIahwkrYzPis9T5fP73QuH6UB76IdXN0JeWfg3Gh9A0oUc/YL78QmcKaM0FrD8JoK8BqYZNd1YAQ=="
}
```

`content` and `title` are encrypted for the issuer _box_ public key.
**Only the message issuer** will be able to decrypt this fields.  

#### `message/outbox`

`content` and `title` are encrypted for the recipient _box_ public key.
**Only the message recipient** will be able to decrypt this fields.

### `invitation/*`

#### `invitation/certification`

 - Get an invitation, by id: `invitation/certification/<id>`
 - Add a new invitation: `invitation/certification` (POST)
 - Delete an existing invitation: `invitation/certification/_delete` (POST)
 - Search on invitations: `invitation/certification/_search` (POST or GET)

### `like/*`

#### `like/record`

All documents stored in this index have a mandatory `kind` attribute, with one of the following values:
 - VIEW
 - LIKE
 - DISLIKE
 - STAR
 - ABUSE

Only record with VIEW kind can be anonymous (no `issuer` and `signature` attributes).

This index allow you to:

 - Count like records by `kind`, on any document (e.g. number of stars on a profile): `/<doc_index>/<doc_type>/_likes`, `<doc_index>/<doc_type>/_abuses`, etc.
 
   * Example: number of likes of a user profile: `/user/profile/<pubkey>/_likes`
      
 - Add a new like record: `like/record` (POST)

 - To delete a existing like record, use: `history/delete` (POST)


### `subscription/*`

#### `subscription/record`

 - Get an subscription, by id: `subscription/record/<id>`
 - Add a new subscription: `subscription/record` (POST)
 - Delete an existing subscription: `subscription/record/_delete` (POST)
 - Search on subscriptions: `subscription/record/_search` (POST or GET)


### `page/*`

#### `page/record`

 - Get a page, by id: `page/record/<id>`
 - Search on pages: `page/record/_search` (POST or GET)
 - Add a new page: `page/record` (POST)
 - Delete an existing page: `page/record/_delete` (POST)


#### `page/comment`

 - Get an page's comment, by id: `page/comment/<id>`
 - Search on comments: `page/comment/_search` (POST or GET)
 - Add a new comment: `page/comment` (POST)
 - Delete an existing comment: `page/comment/_delete` (POST)
 
### `market/*`

#### `market/record`

 - Get a Ad, by id: `market/record/<id>`
 - Search on Ads: `market/record/_search` (POST or GET)
 - Add a new Ad: `market/record` (POST)
 - Delete an existing ad: `market/record/_delete` (POST)

#### `market/comment`

 - Get an ad's comment, by id: `market/comment/<id>`
 - Search on comments: `market/comment/_search` (POST or GET)
 - Add a new comment: `market/comment` (POST)
 - Delete an existing comment: `market/comment/_delete` (POST)