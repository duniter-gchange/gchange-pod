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

import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.AbstractRecordRepositoryImpl;
import org.duniter.elasticsearch.gchange.model.auction.AuctionRecord;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by blavenie on 23/08/2020.
 */
public class AuctionRecordRepositoryImpl
        extends AbstractRecordRepositoryImpl<AuctionRecordRepositoryImpl>
        implements AuctionRecordRepository<AuctionRecordRepositoryImpl> {

    @Inject
    public AuctionRecordRepositoryImpl(PluginSettings pluginSettings) {
        super(AuctionIndexRepository.INDEX, pluginSettings);
    }

    @Override
    public XContentBuilder createTypeMapping() {

        try {
            return XContentFactory.jsonBuilder().startObject().startObject(getType())
                    .startObject("properties")

                    // version
                    .startObject(AuctionRecord.Fields.VERSION)
                    .field("type", "integer")
                    .endObject()

                    // creationTime
                    .startObject(AuctionRecord.Fields.CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(AuctionRecord.Fields.TIME)
                    .field("type", "integer")
                    .endObject()

                    // price
                    .startObject(AuctionRecord.Fields.PRICE)
                    .field("type", "double")
                    .endObject()

                    // price Unit
                    .startObject(AuctionRecord.Fields.UNIT)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // price currency
                    .startObject(AuctionRecord.Fields.CURRENCY)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // issuer
                    .startObject(AuctionRecord.Fields.ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // hash
                    .startObject(AuctionRecord.Fields.HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(AuctionRecord.Fields.SIGNATURE)
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
}
