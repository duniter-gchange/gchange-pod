package org.duniter.elasticsearch.gchange;

/*
 * #%L
 * Duniter4j :: ElasticSearch Plugin
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

import org.duniter.elasticsearch.gchange.dao.market.MarketCommentDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryCommentDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryIndexDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryRecordDao;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.gchange.service.RegistryService;
import org.duniter.elasticsearch.service.DocStatService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

/**
 * Created by blavenie on 17/06/16.
 */
public class PluginInit extends AbstractLifecycleComponent<PluginInit> {

    private final PluginSettings pluginSettings;
    private final ThreadPool threadPool;
    private final Injector injector;
    private final static ESLogger logger = Loggers.getLogger("gchange");

    @Inject
    public PluginInit(Settings settings, PluginSettings pluginSettings, ThreadPool threadPool, final Injector injector) {
        super(settings);
        this.pluginSettings = pluginSettings;
        this.threadPool = threadPool;
        this.injector = injector;
    }

    @Override
    protected void doStart() {
        threadPool.scheduleOnClusterHealthStatus(() -> {
            createIndices();
        }, ClusterHealthStatus.YELLOW, ClusterHealthStatus.GREEN);
    }

    @Override
    protected void doStop() {

    }

    @Override
    protected void doClose() {

    }

    protected void createIndices() {

        boolean reloadIndices = pluginSettings.reloadAllIndices();

        if (reloadIndices) {
            if (logger.isInfoEnabled()) {
                logger.info("Reloading all Gchange indices...");
            }
            injector.getInstance(RegistryService.class)
                    .deleteIndex()
                    .createIndexIfNotExists();
            injector.getInstance(MarketService.class)
                    .deleteIndex()
                    .createIndexIfNotExists();

            if (logger.isInfoEnabled()) {
                logger.info("Reloading all Gchange indices... [OK]");
            }
        }
        else {
            if (logger.isInfoEnabled()) {
                logger.info("Checking Gchange indices...");
            }
            injector.getInstance(RegistryService.class).createIndexIfNotExists();
            injector.getInstance(MarketService.class).createIndexIfNotExists();

            if (logger.isInfoEnabled()) {
                logger.info("Checking Gchange indices... [OK]");
            }
        }

        // Register stats on indices
        if (pluginSettings.enableDocStats()) {
            injector.getInstance(DocStatService.class)
                    .registerIndex(MarketIndexDao.INDEX, MarketRecordDao.TYPE)
                    .registerIndex(MarketIndexDao.INDEX, MarketCommentDao.TYPE)
                    .registerIndex(RegistryIndexDao.INDEX, RegistryRecordDao.TYPE)
                    .registerIndex(RegistryIndexDao.INDEX, RegistryCommentDao.TYPE)
            ;
        }
    }
}
