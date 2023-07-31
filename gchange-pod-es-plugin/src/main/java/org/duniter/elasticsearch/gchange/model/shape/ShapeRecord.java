package org.duniter.elasticsearch.gchange.model.shape;

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
import org.duniter.elasticsearch.model.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blavenie on 01/09/2020.
 */
@Data
@FieldNameConstants
public class ShapeRecord extends Record {

    public static class Fields extends Record.Fields {}

    private Map<String, String> properties = new HashMap<>();
    private Long creationTime;

    private Integer version;

}
