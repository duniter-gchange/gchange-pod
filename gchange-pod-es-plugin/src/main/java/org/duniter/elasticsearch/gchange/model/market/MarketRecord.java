package org.duniter.elasticsearch.gchange.model.market;

/*
 * #%L
 * Duniter4j :: ElasticSearch GChange plugin
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

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by blavenie on 01/12/16.
 */
@Data
@FieldNameConstants
public class MarketRecord extends LightMarketRecord {

    public static class Fields extends LightMarketRecord.Fields {
        public static final String THUMBNAIL_WITH_CONTENT_TYPE = "thumbnail._content_type";
    }

    public enum Type {
        offer,
        need,
        auction,
        crowdfunding;

        public static Set<String> names() {
            return Arrays.stream(values()).map(Type::name).collect(Collectors.toSet());
        }
    }


    private String description;
    private Map<String, String> thumbnail = new HashMap<>();
    private Double price;
    private String unit;
    private Double fees;
    private String feesCurrency;
    private String currency;
    private String category;

}
