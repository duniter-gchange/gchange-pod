package org.duniter.elasticsearch.gchange.dao;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.dao.AbstractIndexTypeDao;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.CommentDao;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import java.io.IOException;

/**
 * Created by Benoit on 30/03/2015.
 */
public class AbstractCommentDaoImpl<T extends AbstractCommentDaoImpl> extends AbstractIndexTypeDao<T> implements CommentDao<T> {


    protected PluginSettings pluginSettings;

    public AbstractCommentDaoImpl(String index, PluginSettings pluginSettings) {
        super(index, CommentDao.TYPE);
        this.pluginSettings = pluginSettings;
    }

    @Override
    protected void createIndex() throws JsonProcessingException {
        throw new TechnicalException("not implemented");
    }

    public String create(final String json) {
        return super.indexDocumentFromJson(json);
    }

    public void update(final String id, final String json) {
        super.updateDocumentFromJson(id, json);
    }

    @Override
    public long countReplies(String id) {

        // Prepare count request
        SearchRequestBuilder searchRequest = client
                .prepareSearch(getIndex())
                .setTypes(getType())
                .setFetchSource(false)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setSize(0);

        // Query = filter on reference
        TermQueryBuilder query = QueryBuilders.termQuery(RecordComment.PROPERTY_REPLY_TO_JSON, id);
        searchRequest.setQuery(query);

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


    public XContentBuilder createTypeMapping() {
        String stringAnalyzer = pluginSettings.getDefaultStringAnalyzer();

        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(getType())
                    .startObject("properties")

                    // issuer
                    .startObject("issuer")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // time
                    .startObject("time")
                    .field("type", "integer")
                    .endObject()

                    // message
                    .startObject("message")
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // record
                    .startObject("record")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // reply to
                    .startObject("reply_to")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // aggregations
                    .startObject("aggregations")
                    .field("type", "nested")
                    .field("dynamic", "true")
                    .startObject("properties")
                    .startObject("reply_count")
                    .field("type", "integer")
                    .field("index", "not_analyzed")
                    .endObject()
                    .endObject()
                    .endObject()

                    .endObject()
                    .endObject().endObject();

            return mapping;
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while getting mapping for index [%s]: %s", getType(), ioe.getMessage()), ioe);
        }
    }

}
