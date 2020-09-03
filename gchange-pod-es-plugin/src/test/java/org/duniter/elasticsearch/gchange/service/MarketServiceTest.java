package org.duniter.elasticsearch.gchange.service;

/*
 * #%L
 * UCoin Java Client :: ElasticSearch Indexer
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.elasticsearch.gchange.TestResource;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecordFilter;
import org.duniter.elasticsearch.service.ServiceLocator;
import org.duniter.elasticsearch.user.service.UserService;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.geo.GeoPoint;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Benoit on 06/05/2015.
 */
@Ignore
public class MarketServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MarketServiceTest.class);

    @ClassRule
    public static final TestResource resource = TestResource.create();

    private MarketService service;

    @Before
    public void setUp() throws Exception {
        service = ServiceLocator.instance().getBean(MarketService.class);

        while(!service.isReady()) {
            Thread.sleep(1000);
        }

        // Init indices
        service.createIndexIfNotExists();
        ServiceLocator.instance().getBean(UserService.class).createIndexIfNotExists();

        Thread.sleep(5000);
    }

    @Test
    public void searchRecord() {

        // Prepare query
        MarketRecordFilter query = new MarketRecordFilter();

        query.setLocation("Paris");
        query.setGeoPoint(new GeoPoint(50,2)); // e.g. Paris position
        query.setGeoDistance("10km");

        query.setSearchText("test");
        query.setWithClosed(true);
        query.setWithOld(true);

        query.setSize(10);

        List<MarketRecord> result = service.findByFilter(query, MarketRecord.class);
    }

}
