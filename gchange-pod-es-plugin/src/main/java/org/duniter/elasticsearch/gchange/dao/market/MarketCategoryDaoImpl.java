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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.duniter.core.client.model.elasticsearch.RecordComment;
import org.duniter.core.client.model.elasticsearch.Records;
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.dao.AbstractIndexTypeDao;
import org.duniter.elasticsearch.dao.handler.AddSequenceAttributeHandler;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.model.auction.AuctionRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketCategoryRecord;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import java.io.IOException;
import java.util.Set;

/**
 * Created by blavenie on 03/04/17.
 */
public class MarketCategoryDaoImpl extends AbstractIndexTypeDao<MarketCategoryDao> implements MarketCategoryDao {

    private static final String CATEGORIES_BULK_CLASSPATH_FILE = "market-categories-bulk-insert.json";

    @Inject
    public MarketCategoryDaoImpl(PluginSettings pluginSettings) {
        super(MarketIndexDao.INDEX, MarketIndexDao.CATEGORY_TYPE);
    }

    @Override
    public String getType() {
        return MarketIndexDao.CATEGORY_TYPE;
    }

    @Override
    protected void createIndex() throws JsonProcessingException {
        throw new TechnicalException("not implemented");
    }

    @Override
    public String create(String id, final String json) {
        IndexResponse response = (IndexResponse)this.client.prepareIndex(this.getIndex(), this.getType())
                .setSource(json)
                .setId(id)
                .setRefresh(false)
                .execute().actionGet();
        return response.getId();
    }

    @Override
    public void update(final String id, final String json) {
        client.updateDocumentFromJson(MarketIndexDao.INDEX, MarketIndexDao.CATEGORY_TYPE, id, json);
    }

    @Override
    public XContentBuilder createTypeMapping() {
        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                    .startObject(MarketIndexDao.CATEGORY_TYPE)
                    .startObject("properties")

                    // name
                    .startObject(MarketCategoryRecord.PROPERTY_NAME)
                    .field("type", "string")
                    .field("analyzer", pluginSettings.getDefaultStringAnalyzer())
                    .endObject()

                    // parent
                    .startObject(MarketCategoryRecord.PROPERTY_PARENT)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // version
                    .startObject(MarketCategoryRecord.PROPERTY_VERSION)
                    .field("type", "integer")
                    .endObject()

                    // creationTime
                    .startObject(Records.PROPERTY_CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(MarketCategoryRecord.PROPERTY_TIME)
                    .field("type", "integer")
                    .endObject()

                    // issuer
                    .startObject(MarketCategoryRecord.PROPERTY_ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // hash
                    .startObject(MarketCategoryRecord.PROPERTY_HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(MarketCategoryRecord.PROPERTY_SIGNATURE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // localized names
                    .startObject(MarketCategoryRecord.PROPERTY_LOCALIZED_NAMES)
                    .field("type", "nested")
                    .field("dynamic", "true")
                        .startObject("properties")
                            .startObject("en")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                            .endObject()
                            .startObject("fr-FR")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                            .endObject()
                        .endObject()
                    .endObject()

                    .endObject()
                    .endObject().endObject();

            return mapping;
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while getting mapping for index [%s/%s]: %s", getIndex(), getType(), ioe.getMessage()), ioe);
        }
    }


    public void fillCategories() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[%s/%s] Fill data", getIndex(), getType()));
        }

        // Insert categories
        client.bulkFromClasspathFile(CATEGORIES_BULK_CLASSPATH_FILE, getIndex(), getType(),
                // Add order attribute
                new AddSequenceAttributeHandler("order", "\\{.*\"name\".*\\}", 1));
    }

    public long count(QueryBuilder query) {
        // Prepare count request
        SearchRequestBuilder searchRequest = client
                .prepareSearch(getIndex())
                .setTypes(getType())
                .setFetchSource(false)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setSize(0);

        // Query
        if (query != null) {
            searchRequest.setQuery(query);
        }

        // Execute query
        try {
            SearchResponse response = searchRequest.execute().actionGet();
            return response.getHits().getTotalHits();
        }
        catch(SearchPhaseExecutionException e) {
            // Failed or no item on index
            logger.error(String.format("Error while counting comment replies: %s", e.getMessage()), e);
        }
        return 1;
    }

    @Override
    public void startDataMigration() {
        // no categories: fill it
        long count = count(null);
        if (count == 0L) {
            fillCategories();
            return;
        }

        // THere is data: detected if only OLD data
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.existsQuery(MarketCategoryRecord.PROPERTY_TIME));
        count = count(query);
        if (count == 0) {
            logger.info("Migrating market categories... (delete then recreate)");

            // Delete all, then fill it again
            Set<String> ids = getAllIds();
            for (String id: ids) {
                // Delete the document
                client.prepareDelete(getIndex(), getType(), id).execute().actionGet();
            }

            fillCategories();
        }


    }

    @Override
    public Set<String> getAllIds() {
        SearchRequestBuilder request = client.prepareSearch(getIndex())
                .setTypes(getType())
                .setSize(pluginSettings.getIndexBulkSize())
                .setFetchSource(false);

        return executeAndGetIds(request.execute().actionGet());
    }
}
