package org.duniter.elasticsearch.gchange.dao.auction;

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
import org.duniter.elasticsearch.dao.AbstractIndexRepository;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.common.inject.Inject;

/**
 * Created by blavenie on 23/08/2020.
 */
public class AuctionIndexRepositoryImpl
    extends AbstractIndexRepository<AuctionIndexRepository>
    implements AuctionIndexRepository {

    private PluginSettings pluginSettings;
    private AuctionRecordRepository recordRepository;

    @Inject
    public AuctionIndexRepositoryImpl(PluginSettings pluginSettings,
                                      AuctionRecordRepository recordRepository) {
        super(AuctionIndexRepository.INDEX);

        this.pluginSettings = pluginSettings;
        this.recordRepository = recordRepository;
    }


    @Override
    protected void createIndex() throws JsonProcessingException {
        logger.info(String.format("Creating index [%s]", INDEX));

        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(INDEX);
        org.elasticsearch.common.settings.Settings indexSettings = org.elasticsearch.common.settings.Settings.settingsBuilder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1)
                //.put("analyzer", createDefaultAnalyzer())
                .build();
        createIndexRequestBuilder.setSettings(indexSettings);
        createIndexRequestBuilder.addMapping(recordRepository.getType(), recordRepository.createTypeMapping());
        createIndexRequestBuilder.execute().actionGet();

    }

}
