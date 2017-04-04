package org.duniter.elasticsearch.gchange.dao;

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

import org.duniter.core.beans.Bean;
import org.duniter.elasticsearch.client.Duniter4jClientImpl;
import org.duniter.elasticsearch.gchange.dao.market.*;
import org.duniter.elasticsearch.gchange.dao.registry.*;
import org.duniter.elasticsearch.service.ServiceLocator;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.Module;

public class DaoModule extends AbstractModule implements Module {

    @Override protected void configure() {

        bind(RegistryIndexDao.class).to(RegistryIndexDaoImpl.class).asEagerSingleton();
        bind(RegistryCommentDao.class).to(RegistryCommentDaoImpl.class).asEagerSingleton();
        bind(RegistryRecordDao.class).to(RegistryRecordDaoImpl.class).asEagerSingleton();

        bind(MarketIndexDao.class).to(MarketIndexDaoImpl.class).asEagerSingleton();
        bind(MarketCommentDao.class).to(MarketCommentDaoImpl.class).asEagerSingleton();
        bind(MarketRecordDao.class).to(MarketRecordDaoImpl.class).asEagerSingleton();
    }

    /* protected methods */

    protected <T extends Bean> void bindWithLocator(Class<T> clazz) {
        bind(clazz).toProvider(new ServiceLocator.Provider<>(clazz));
    }

}