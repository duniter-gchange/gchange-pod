package org.duniter.elasticsearch.gchange.rest;

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

import org.duniter.elasticsearch.gchange.rest.market.*;
import org.duniter.elasticsearch.gchange.rest.registry.*;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.Module;

public class RestModule extends AbstractModule implements Module {

    @Override protected void configure() {

        // Market
        bind(RestMarketRecordIndexAction.class).asEagerSingleton();
        bind(RestMarketRecordUpdateAction.class).asEagerSingleton();
        bind(RestMarketCommentIndexAction.class).asEagerSingleton();
        bind(RestMarketCommentUpdateAction.class).asEagerSingleton();
        bind(RestMarketCategoryAction.class).asEagerSingleton();
        bind(RestMarketImageAction.class).asEagerSingleton();

        // Registry
        bind(RestRegistryRecordIndexAction.class).asEagerSingleton();
        bind(RestRegistryRecordUpdateAction.class).asEagerSingleton();
        bind(RestRegistryCommentIndexAction.class).asEagerSingleton();
        bind(RestRegistryCommentUpdateAction.class).asEagerSingleton();
        bind(RestRegistryCategoryAction.class).asEagerSingleton();
        bind(RestRegistryImageAction.class).asEagerSingleton();
    }
}