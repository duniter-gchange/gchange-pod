package org.duniter.elasticsearch.gchange.dao.registry;

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

import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.AbstractRecordDaoImpl;
import org.elasticsearch.common.inject.Inject;

/**
 * Created by blavenie on 03/04/17.
 */
public class RegistryRecordDaoImpl extends AbstractRecordDaoImpl implements RegistryRecordDao {

    @Inject
    public RegistryRecordDaoImpl(PluginSettings pluginSettings) {
        super(RegistryIndexDao.INDEX, pluginSettings);

    }
}
