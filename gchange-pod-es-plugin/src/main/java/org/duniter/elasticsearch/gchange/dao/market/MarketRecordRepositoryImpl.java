package org.duniter.elasticsearch.gchange.dao.market;

/*
 * #%L
 * Ğchange Pod :: ElasticSearch plugin
 * %%
 * Copyright (C) 2014 - 2017 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.duniter.core.client.repositories.CurrencyRepository;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.util.ArrayUtils;
import org.duniter.core.util.Beans;
import org.duniter.core.util.cache.Cache;
import org.duniter.core.util.cache.SimpleCache;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.AbstractRecordRepositoryImpl;
import org.duniter.elasticsearch.gchange.model.market.CategoryRecord;
import org.duniter.elasticsearch.gchange.model.market.LightCategory;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecordFilter;
import org.duniter.elasticsearch.model.Records;
import org.duniter.elasticsearch.util.bytes.JsonNodeBytesReference;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by blavenie on 03/04/17.
 */
public class MarketRecordRepositoryImpl extends AbstractRecordRepositoryImpl implements MarketRecordRepository {

    private final int CURRENCY_CACHE_DURATION_MS = 10*60*1000; // 10 min
    private final String CURRENCY_CACHE_KEY_ALL = "ALL";

    private Cache<String, String[]> currencyIdsCache;

    @Inject
    public MarketRecordRepositoryImpl(PluginSettings pluginSettings,
                                      final CurrencyRepository currencyRepository) {
        super(MarketIndexRepository.INDEX, pluginSettings);

        // Init currency cache (lazy)
        currencyIdsCache = new SimpleCache<String, String[]>(CURRENCY_CACHE_DURATION_MS) {
            @Override
            public String[] load(String key) {
                return Beans.toArray(currencyRepository.findAllIds(), String[]::new);
            }
        };
    }


    @Override
    public <C> List<C> findByFilter(MarketRecordFilter filter, Class<? extends C> clazz, String... fieldNames){

        try {
            SearchRequestBuilder request = client.prepareSearch(MarketIndexRepository.INDEX)
                    .setTypes(MarketRecordRepository.TYPE)
                    .setSize(filter.getSize())
                    .setFrom(filter.getFrom())
                    .setFetchSource(fieldNames, null);


            BoolQueryBuilder query = QueryBuilders.boolQuery();
            List<QueryBuilder> matches = Lists.newArrayList();
            List<QueryBuilder> filters = Lists.newArrayList();

            // Match : text
            String text = filter.getSearchText();
            if (StringUtils.isNotBlank(text)) {
                String lowerText = text.toLowerCase();

                // Multi match on title, description
                matches.add(QueryBuilders.multiMatchQuery(lowerText,
                        MarketRecord.Fields.TITLE + "^2",
                        MarketRecord.Fields.DESCRIPTION)
                .type(MatchQueryBuilder.Type.PHRASE_PREFIX));

                // Match on title
                matches.add(QueryBuilders.matchQuery(MarketRecord.Fields.TITLE, lowerText).boost(2));

                // Match on title as prefix
                matches.add(QueryBuilders.matchPhrasePrefixQuery(MarketRecord.Fields.TITLE, lowerText));

                // Match on description
                matches.add(QueryBuilders.matchQuery(MarketRecord.Fields.DESCRIPTION, lowerText));

                // Match on category
                matches.add(QueryBuilders.nestedQuery(MarketRecord.Fields.CATEGORY,
                        QueryBuilders.matchQuery(MarketRecord.Fields.CATEGORY + "." + CategoryRecord.Fields.NAME, lowerText)));
            }

            // Filter: type
            if (filter.getType() != null) {
                filters.add(QueryBuilders.termQuery(MarketRecord.Fields.TYPE, filter.getType().name()));
            }

            // Filter: category
            LightCategory category = filter.getCategory();
            if (category != null) {
                if (category.getId() != null) {
                    filters.add(QueryBuilders.nestedQuery(
                            MarketRecord.Fields.CATEGORY,
                            QueryBuilders.termQuery(MarketRecord.Fields.CATEGORY +"."+ CategoryRecord.Fields.ID, category.getId())));
                }
            }


            // Filter: tags
            String[] tags = parseTags(text);
            if (ArrayUtils.isNotEmpty(tags)) {
                filters.add(QueryBuilders.termsQuery(Records.Fields.TAGS, tags));
            }

            // Filter: with closed ?
            if (!filter.isWithClosed()) {
                // Filter on stock > 0
                filters.add(QueryBuilders.rangeQuery(MarketRecord.Fields.STOCK).gt(0));
            }

            // Filter: with old ?
            if (!filter.isWithOld()) {
                // Filter on time > last year
                long minTime = (System.currentTimeMillis() / 1000) - 24 * 365 * 60 * 60;
                // Round to hour, to be able to use cache (avoid to many similar requests)
                minTime = Math.round(Math.floor(minTime / 60 / 60 ) * 60 * 60);
                filters.add(QueryBuilders.rangeQuery(MarketRecord.Fields.TIME).gte(minTime));
            }

            // Filter: currency
            String currency = filter.getCurrency();
            if (StringUtils.isNotBlank(currency)) {
                if (currency.indexOf(',') == -1) {
                    filters.add(QueryBuilders.termQuery(MarketRecord.Fields.CURRENCY, currency));
                }
                else {
                    filters.add(QueryBuilders.termsQuery(MarketRecord.Fields.CURRENCY, currency.split(",")));
                }
            }
            else {
                // Filter on currencies
                String[] currencyIds = currencyIdsCache.get(CURRENCY_CACHE_KEY_ALL);
                if (ArrayUtils.isNotEmpty(currencyIds)) {
                    if (currencyIds.length == 1) {
                        filters.add(QueryBuilders.termQuery(MarketRecord.Fields.CURRENCY, currencyIds[0]));
                    } else {
                        filters.add(QueryBuilders.termsQuery(MarketRecord.Fields.CURRENCY, currencyIds));
                    }
                }
            }

            if (matches.size() > 0) {
                matches.forEach(query::should);
                query.minimumNumberShouldMatch(1);
            }
            if (filters.size() > 0) {
                filters.forEach(query::filter);
            }

            // Execute request
            SearchResponse response = request.setQuery(query).execute().get();

            // Fetch and serialize

            // Use byte ref if possible
            if (BytesReference.class.isAssignableFrom(clazz)) {
                final ObjectMapper objectMapper = getObjectMapper();
                return (List<C>) toList(response, hit -> new JsonNodeBytesReference(hit.getSourceRef().toBytesArray(), objectMapper));
            }

            // Fetch and serialize
            return toList(response, clazz);
        } catch( InterruptedException e) {
            throw new ElasticsearchException(e);
        } catch(ExecutionException e) {
            throw new ElasticsearchException("Error during market search: " + e.getMessage(), e);
        }
    }

    @Override
    public XContentBuilder createTypeMapping() {
        String stringAnalyzer = pluginSettings.getDefaultStringAnalyzer();

        try {
            return XContentFactory.jsonBuilder().startObject().startObject(getType())
                    .startObject("properties")

                    // version
                    .startObject(MarketRecord.Fields.VERSION)
                    .field("type", "integer")
                    .endObject()

                    // title
                    .startObject(MarketRecord.Fields.TITLE)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // description
                    .startObject(MarketRecord.Fields.DESCRIPTION)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // creationTime
                    .startObject(MarketRecord.Fields.CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(MarketRecord.Fields.TIME)
                    .field("type", "integer")
                    .endObject()

                    // price
                    .startObject(MarketRecord.Fields.PRICE)
                    .field("type", "double")
                    .endObject()

                    // price Unit
                    .startObject(MarketRecord.Fields.UNIT)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // price currency
                    .startObject(MarketRecord.Fields.CURRENCY)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // fees
                    .startObject(MarketRecord.Fields.FEES)
                    .field("type", "double")
                    .endObject()

                    // fees currency
                    .startObject(MarketRecord.Fields.FEES_CURRENCY)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // stock
                    .startObject(MarketRecord.Fields.STOCK)
                    .field("type", "integer")
                    .endObject()

                    // issuer
                    .startObject(MarketRecord.Fields.ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // type (offer, need, ...)
                    .startObject(MarketRecord.Fields.TYPE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // address
                    .startObject(Records.Fields.ADDRESS)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // city
                    .startObject(Records.Fields.CITY)
                    .field("type", "string")
                    .endObject()

                    // geoPoint
                    .startObject(Records.Fields.GEO_POINT)
                    .field("type", "geo_point")
                    .endObject()

                    // thumbnail
                    .startObject(MarketRecord.Fields.THUMBNAIL)
                    .field("type", "attachment")
                    .startObject("fields") // src
                    .startObject("content") // title
                    .field("index", "no")
                    .endObject()
                    .startObject("title") // title
                    .field("type", "string")
                    .field("store", "no")
                    .endObject()
                    .startObject("author") // title
                    .field("store", "no")
                    .endObject()
                    .startObject("content_type") // title
                    .field("store", "yes")
                    .endObject()
                    .endObject()
                    .endObject()

                    // pictures
                    .startObject(Records.Fields.PICTURES)
                    .field("type", "nested")
                    .field("dynamic", "false")
                    .startObject("properties")
                    .startObject("file") // file
                    .field("type", "attachment")
                    .startObject("fields")
                    .startObject("content") // content
                    .field("index", "no")
                    .endObject()
                    .startObject("title") // title
                    .field("type", "string")
                    .field("store", "yes")
                    .field("analyzer", stringAnalyzer)
                    .endObject()
                    .startObject("author") // author
                    .field("type", "string")
                    .field("store", "no")
                    .endObject()
                    .startObject("content_type") // content_type
                    .field("store", "yes")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject()

                    // picturesCount
                    .startObject(Records.Fields.PICTURES_COUNT)
                    .field("type", "integer")
                    .endObject()

                    // category
                    .startObject(MarketRecord.Fields.CATEGORY)
                    .field("type", "nested")
                    .field("dynamic", "false")
                    .startObject("properties")
                    .startObject("id") // id
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()
                    .startObject("parent") // parent
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()
                    .startObject("name") // name
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()
                    .endObject()
                    .endObject()

                    // tags
                    .startObject(Records.Fields.TAGS)
                    .field("type", "completion")
                    .field("search_analyzer", "simple")
                    .field("analyzer", "simple")
                    .field("preserve_separators", "false")
                    .endObject()

                    // hash
                    .startObject(MarketRecord.Fields.HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(MarketRecord.Fields.SIGNATURE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    .endObject()
                    .endObject().endObject();
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while getting mapping for index [%s/%s]: %s", getIndex(), getType(), ioe.getMessage()), ioe);
        }
    }

    @Override
    public void startDataMigration() {



    }

    protected String[] parseTags(String text) {
        // TODO
        return null;
    }
}
