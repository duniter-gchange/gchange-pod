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

import org.duniter.core.client.model.elasticsearch.Records;
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.AbstractRecordDaoImpl;
import org.duniter.elasticsearch.gchange.model.auction.AuctionRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by blavenie on 03/04/17.
 */
public class MarketRecordDaoImpl extends AbstractRecordDaoImpl implements MarketRecordDao {

    @Inject
    public MarketRecordDaoImpl(PluginSettings pluginSettings) {
        super(MarketIndexDao.INDEX, pluginSettings);
    }

    @Override
    public XContentBuilder createTypeMapping() {
        String stringAnalyzer = pluginSettings.getDefaultStringAnalyzer();

        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(getType())
                    .startObject("properties")

                    // version
                    .startObject(MarketRecord.PROPERTY_VERSION)
                    .field("type", "integer")
                    .endObject()

                    // title
                    .startObject(MarketRecord.PROPERTY_TITLE)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // description
                    .startObject(MarketRecord.PROPERTY_DESCRIPTION)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // creationTime
                    .startObject(MarketRecord.PROPERTY_CREATION_TIME)
                    .field("type", "integer")
                    .endObject()

                    // time
                    .startObject(MarketRecord.PROPERTY_TIME)
                    .field("type", "integer")
                    .endObject()

                    // price
                    .startObject(MarketRecord.PROPERTY_PRICE)
                    .field("type", "double")
                    .endObject()

                    // price Unit
                    .startObject(MarketRecord.PROPERTY_UNIT)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // price currency
                    .startObject(MarketRecord.PROPERTY_CURRENCY)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // fees
                    .startObject(MarketRecord.PROPERTY_FEES)
                    .field("type", "double")
                    .endObject()

                    // fees currency
                    .startObject(MarketRecord.PROPERTY_FEES_CURRENCY)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // stock
                    .startObject(MarketRecord.PROPERTY_STOCK)
                    .field("type", "integer")
                    .endObject()

                    // issuer
                    .startObject(MarketRecord.PROPERTY_ISSUER)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // type (offer, need, ...)
                    .startObject(MarketRecord.PROPERTY_TYPE)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // address
                    .startObject(MarketRecord.PROPERTY_ADDRESS)
                    .field("type", "string")
                    .field("analyzer", stringAnalyzer)
                    .endObject()

                    // city
                    .startObject(MarketRecord.PROPERTY_CITY)
                    .field("type", "string")
                    .endObject()

                    // geoPoint
                    .startObject(MarketRecord.PROPERTY_GEO_POINT)
                    .field("type", "geo_point")
                    .endObject()

                    // thumbnail
                    .startObject(MarketRecord.PROPERTY_THUMBNAIL)
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
                    .startObject(MarketRecord.PROPERTY_PICTURES)
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
                    .startObject(MarketRecord.PROPERTY_PICTURES_COUNT)
                    .field("type", "integer")
                    .endObject()

                    // category
                    .startObject(MarketRecord.PROPERTY_CATEGORY)
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
                    .startObject(MarketRecord.PROPERTY_TAGS)
                    .field("type", "completion")
                    .field("search_analyzer", "simple")
                    .field("analyzer", "simple")
                    .field("preserve_separators", "false")
                    .endObject()

                    // hash
                    .startObject(MarketRecord.PROPERTY_HASH)
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    // signature
                    .startObject(MarketRecord.PROPERTY_SIGNATURE)
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
}
