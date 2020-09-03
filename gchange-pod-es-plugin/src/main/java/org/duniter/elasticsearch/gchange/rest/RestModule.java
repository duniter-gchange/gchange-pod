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

import org.duniter.elasticsearch.gchange.rest.like.RestMarketLikeAction;
import org.duniter.elasticsearch.gchange.rest.market.*;
import org.duniter.elasticsearch.gchange.rest.market.search.CustomSearchAction;
import org.duniter.elasticsearch.gchange.rest.mixed.RestMixedSearchAction;
import org.duniter.elasticsearch.gchange.rest.shape.RestShapeIndexAction;
import org.duniter.elasticsearch.gchange.rest.shape.RestShapeUpdateAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.Module;

public class RestModule extends AbstractModule implements Module {

    @Override protected void configure() {

        // Market
        bind(RestMarketRecordIndexAction.class).asEagerSingleton();
        bind(RestMarketRecordUpdateAction.class).asEagerSingleton();
        bind(RestMarketCommentIndexAction.class).asEagerSingleton();
        bind(RestMarketCommentUpdateAction.class).asEagerSingleton();
        bind(RestMarketImageAction.class).asEagerSingleton();
        bind(RestMarketShareLinkAction.class).asEagerSingleton();

        // Custom search
        bind(CustomSearchAction.class).asEagerSingleton();

        // Market category
        bind(RestMarketCategoryIndexAction.class).asEagerSingleton();
        bind(RestMarketCategoryUpdateAction.class).asEagerSingleton();

        // Mixed search, on market indices
        bind(RestMixedSearchAction.class).asEagerSingleton();

        // Like, on market indices
        bind(RestMarketLikeAction.class).asEagerSingleton();

        // Shape
        bind(RestShapeIndexAction.class).asEagerSingleton();
        bind(RestShapeUpdateAction.class).asEagerSingleton();

    }
}