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
import org.duniter.core.client.model.elasticsearch.Records;
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.dao.AbstractIndexTypeDao;
import org.duniter.elasticsearch.dao.handler.AddSequenceAttributeHandler;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.model.market.CategoryRecord;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

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
                    .startObject(CategoryRecord.PROPERTY_NAME)
                    .field("type", "string")
                    .field("analyzer", pluginSettings.getDefaultStringAnalyzer())
                    .endObject()

                    // parent
                    .startObject(CategoryRecord.PROPERTY_PARENT)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // version
                    .startObject(CategoryRecord.PROPERTY_VERSION)
                    .field("type", "integer")
                    .endObject()

                    // creationTime
                    .startObject(Records.PROPERTY_CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(CategoryRecord.PROPERTY_TIME)
                    .field("type", "integer")
                    .endObject()

                    // issuer
                    .startObject(CategoryRecord.PROPERTY_ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // hash
                    .startObject(CategoryRecord.PROPERTY_HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(CategoryRecord.PROPERTY_SIGNATURE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // localized names
                    .startObject(CategoryRecord.PROPERTY_LOCALIZED_NAMES)
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

                    // order
                    .startObject(CategoryRecord.PROPERTY_ORDER)
                    .field("type", "integer")
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



    @Override
    public void startDataMigration() {
        // no categories: fill it
        long total = this.count(null);
        if (total == 0L) {
            fillCategories();
            return;
        }

        // There is data, BUT only OLD data (not issuer or time)
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.existsQuery(CategoryRecord.PROPERTY_LOCALIZED_NAMES));
        total = this.count(query);
        if (total == 0) {
            logger.info("Migrating market categories... (delete then recreate)");

            // Delete all
            Set<String> ids = getAllIds();
            for (String id: ids) {
                client.prepareDelete(getIndex(), getType(), id).execute().actionGet();
            }

            // then fill categories, from file
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
