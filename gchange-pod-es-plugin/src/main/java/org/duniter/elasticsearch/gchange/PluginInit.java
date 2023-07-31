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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.duniter.core.client.model.local.Peer;
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordRepository;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.gchange.service.NetworkService;
import org.duniter.elasticsearch.gchange.service.PeerService;
import org.duniter.elasticsearch.gchange.service.ShapeService;
import org.duniter.elasticsearch.model.user.LikeRecord;
import org.duniter.elasticsearch.service.DocStatService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.Closeable;
import java.util.Collection;

/**
 * Created by blavenie on 17/06/16.
 */
public class PluginInit extends AbstractLifecycleComponent<PluginInit> {

    private final PluginSettings pluginSettings;
    private final ThreadPool threadPool;
    private final Injector injector;
    private final ESLogger logger;

    @Inject
    public PluginInit(Settings settings,
                      PluginSettings pluginSettings,
                      ThreadPool threadPool,
                      final Injector injector) {
        super(settings);
        this.logger = Loggers.getLogger("gchange", settings, new String[0]);
        this.pluginSettings = pluginSettings;
        this.threadPool = threadPool;
        this.injector = injector;
    }

    @Override
    protected void doStart() {

        // Config the statistics on market documents
        configDocStats();

        threadPool.onMasterStart(() -> {
            // Make sure all indices exists
            createIndices();

            threadPool.scheduleOnClusterReady(() -> {

                // Start peer indexation (and wait)
                startIndexGchangePeers();

            });
        });
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
                logger.info("Reloading all indices...");
            }
            injector.getInstance(MarketService.class)
                    .deleteIndex()
                    .createIndexIfNotExists();

            injector.getInstance(ShapeService.class)
                    .deleteIndex()
                    .createIndexIfNotExists();

            if (logger.isInfoEnabled()) {
                logger.info("Reloading all indices... [OK]");
            }
        }
        else {
            if (logger.isInfoEnabled()) {
                logger.info("Checking indices...");
            }
            injector.getInstance(MarketService.class)
                    .createIndexIfNotExists()
                    // Migrate if need
                    .startDataMigration();

            injector.getInstance(ShapeService.class)
                    .createIndexIfNotExists()
                    // Migrate if need
                    .startDataMigration();

            if (logger.isInfoEnabled()) {
                logger.info("Checking indices [OK]");
            }
        }

    }

    protected void configDocStats() {

        // Register stats on indices
        if (pluginSettings.enableDocStats()) {
            DocStatService docStatService = injector.getInstance(DocStatService.class)
                    .registerIndex(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE)
                    .registerIndex(MarketIndexRepository.INDEX, MarketCommentRepository.TYPE);

            // Add stats on opened and recent Ads by types (offer, need, etc.)
            Long lastYearTime = System.currentTimeMillis() / 1000 - (60 * 60 * 24 * 365); // Filter on one year
            for (MarketRecord.Type type: MarketRecord.Type.values()) {
                QueryBuilder query = QueryBuilders.constantScoreQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(MarketRecord.Fields.TYPE, type.name()))
                        .filter(QueryBuilders.rangeQuery(MarketRecord.Fields.STOCK).gt(1))
                        .filter(QueryBuilders.rangeQuery(MarketRecord.Fields.TIME).gte(lastYearTime))
                );
                String queryName = Joiner.on('_').join(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, "opened", type.name().toLowerCase());
                docStatService.registerIndex(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, queryName, query, null);
            }

            // Add stats on Ads by likes
            for (LikeRecord.Kind kind: LikeRecord.Kind.values()) {
                QueryBuilder query = QueryBuilders.constantScoreQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(LikeRecord.Fields.KIND, kind.name()))
                );

                // Add stats by Like kinds, on market ad
                String queryName = Joiner.on('_').join(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, kind.name().toLowerCase());
                docStatService.registerIndex(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, queryName, query, null);

            }
        }
    }


    protected void startIndexGchangePeers() {

        checkMasterNode();

        try {

            final PeerService peerService = injector.getInstance(PeerService.class);
            final NetworkService networkService = injector.getInstance(NetworkService.class);

            // Get configured peers (with Gchange API)
            Collection<Peer> peers = networkService.getConfigPeersWithGchangeApi();

            // index /network/peers
            peers.forEach(peerService::indexPeers);

            // Start listening new peers
            Closeable close = networkService.startListeningPeers().orElse(null);
            // Stop to listen, if master stop
            if (close != null) {
                threadPool.scheduleOnMasterFirstStop(close);
            }

        } catch (Throwable e) {
            logger.error(String.format("Indexing gchange peers error: %s", e.getMessage()), e);
            throw e;
        }
    }

    protected void checkMasterNode() {
        Preconditions.checkArgument(threadPool.isMasterNode(), "Node must be the master node to execute this job");
    }

}
