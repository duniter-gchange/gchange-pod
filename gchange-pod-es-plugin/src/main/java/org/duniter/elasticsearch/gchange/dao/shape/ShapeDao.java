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

import org.duniter.elasticsearch.dao.IndexTypeRepository;

/**
 * Created by blavenie
 */
public interface ShapeDao extends IndexTypeRepository<ShapeDao, String> {
    String INDEX = "shape";
    String TYPE = "record";

    String create(final String id, final String json);

    void update(final String id, final String json);

    void startDataMigration();
}
