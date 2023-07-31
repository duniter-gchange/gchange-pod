package org.duniter.elasticsearch.gchange.rest.market;

/*
 * #%L
 * duniter4j-elasticsearch-plugin
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

import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordRepository;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.rest.security.RestSecurityController;
import org.elasticsearch.common.inject.Inject;

public class RestMarketImageAction {

    @Inject
    public RestMarketImageAction(RestSecurityController securityController) {

        // Allow to get thumbnail
        securityController.allowImageAttachment(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, MarketRecord.Fields.THUMBNAIL);

        // TODO : allow to get pictures
    }
}