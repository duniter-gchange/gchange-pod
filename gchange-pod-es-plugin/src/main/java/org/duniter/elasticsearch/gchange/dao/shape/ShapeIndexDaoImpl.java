package org.duniter.elasticsearch.gchange.dao.shape;

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

import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.dao.AbstractIndexTypeDao;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.exception.InvalidShapeException;
import org.duniter.elasticsearch.gchange.model.shape.ShapeRecord;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;

/**
 * Created by blavenie on 03/09/20.
 */
public class ShapeIndexDaoImpl extends AbstractIndexTypeDao<ShapeDao> implements ShapeDao {

    private static final String SHAPE_BULK_CLASSPATH_FILE = "shape-record-bulk-insert.json";

    private PluginSettings pluginSettings;

    @Inject
    public ShapeIndexDaoImpl(PluginSettings pluginSettings) {
        super(ShapeDao.INDEX, TYPE);

        this.pluginSettings = pluginSettings;
    }


    @Override
    protected void createIndex() {
        logger.info(String.format("Creating index [%s]", INDEX));

        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(INDEX);
        org.elasticsearch.common.settings.Settings indexSettings = org.elasticsearch.common.settings.Settings.settingsBuilder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1)
                //.put("analyzer", createDefaultAnalyzer())
                .build();
        createIndexRequestBuilder.setSettings(indexSettings);
        createIndexRequestBuilder.addMapping(TYPE, createTypeMapping());
        createIndexRequestBuilder.execute().actionGet();

        // Fill country shapes
        fillCountryShapes();
    }

    @Override
    public String create(String id, final String json) {
        try {
            IndexResponse response = this.client.prepareIndex(this.getIndex(), this.getType())
                    .setSource(json)
                    .setId(id)
                    .setRefresh(false)
                    .execute().actionGet();
            return response.getId();
        } catch (MapperParsingException e) {
            throw new InvalidShapeException(e);
        }
    }

    @Override
    public void update(final String id, final String json) {
        try {
            client.updateDocumentFromJson(this.getIndex(), this.getType(), id, json);
        } catch (MapperParsingException e) {
            throw new InvalidShapeException(e);
        }
    }

    @Override
    public void startDataMigration() {

        // --- for DEV ONLY ---
        if (false) {
            // Recreate the index
            deleteIndex();
            try {Thread.sleep(5 * 1000);} catch(Exception e) {/*silent*/}
            createIndex();
        }

        // Count shapes for country
        long total = count(null);
        if (total == 0) {
            fillCountryShapes();
        }

    }

    @Override
    public String getType() {
        return TYPE;
    }


    @Override
    public XContentBuilder createTypeMapping() {
        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                    .startObject(TYPE)
                    .startObject("properties")

                    // type
                    .startObject("type")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // geometry
                    .startObject("geometry")
                    .field("type", "geo_shape")
                    .field("precision", "5km")
                    .endObject()

                    // properties
                    .startObject(ShapeRecord.PROPERTY_PROPERTIES)
                        .field("type", "nested")
                        .field("dynamic", "true")
                        .startObject("properties")

                            // id
                            .startObject("id")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                            .endObject()

                            // country
                            .startObject("country")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                            .endObject()

                            // title
                            .startObject("title")
                            .field("type", "string")
                            .field("analyzer", pluginSettings.getDefaultStringAnalyzer())
                            .endObject()

                            // position
                            .startObject("position")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                            .endObject()

                            // order
                            .startObject("order")
                            .field("type", "integer")
                            .endObject()
                        .endObject()
                    .endObject()

                    // version
                    .startObject(ShapeRecord.PROPERTY_VERSION)
                    .field("type", "integer")
                    .endObject()

                    // creationTime
                    .startObject(ShapeRecord.PROPERTY_CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(ShapeRecord.PROPERTY_TIME)
                    .field("type", "integer")
                    .endObject()

                    // issuer
                    .startObject(ShapeRecord.PROPERTY_ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // hash
                    .startObject(ShapeRecord.PROPERTY_HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(ShapeRecord.PROPERTY_SIGNATURE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    .endObject()
                    .endObject().endObject();

            return mapping;
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while getting mapping for index [%s/%s]: %s", getIndex(), getType(), ioe.getMessage()), ioe);
        }
    }

    /* -- protected method -- */
    
    protected void fillCountryShapes() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[%s/%s] Fill data", getIndex(), getType()));
        }

        // Insert categories
        client.bulkFromClasspathFile(SHAPE_BULK_CLASSPATH_FILE, getIndex(), getType());
    }

}
