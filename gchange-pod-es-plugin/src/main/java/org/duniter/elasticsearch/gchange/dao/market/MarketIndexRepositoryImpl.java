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

import org.duniter.elasticsearch.dao.AbstractIndexRepository;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.CommentRepository;
import org.duniter.elasticsearch.user.dao.RecordRepository;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.common.inject.Inject;

/**
 * Created by blavenie on 03/04/17.
 */
public class MarketIndexRepositoryImpl extends AbstractIndexRepository<MarketIndexRepository> implements MarketIndexRepository {


    private PluginSettings pluginSettings;
    private RecordRepository recordRepository;
    private CommentRepository commentRepository;
    private MarketCategoryRepository categoryRepository;

    @Inject
    public MarketIndexRepositoryImpl(PluginSettings pluginSettings, MarketRecordRepository recordRepository, MarketCommentRepository commentRepository,
                                     MarketCategoryRepository categoryRepository) {
        super(MarketIndexRepository.INDEX);

        this.pluginSettings = pluginSettings;
        this.commentRepository = commentRepository;
        this.recordRepository = recordRepository;
        this.categoryRepository = categoryRepository;
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
        createIndexRequestBuilder.addMapping(recordRepository.getType(), recordRepository.createTypeMapping());
        createIndexRequestBuilder.addMapping(commentRepository.getType(), commentRepository.createTypeMapping());
        createIndexRequestBuilder.addMapping(categoryRepository.getType(), categoryRepository.createTypeMapping());
        createIndexRequestBuilder.execute().actionGet();

        // Load categories
        categoryRepository.fillCategories();
    }

    public void startDataMigration() {

        // Check if categories must be filled
        categoryRepository.startDataMigration();
    }
}
