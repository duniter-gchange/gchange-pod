package org.duniter.elasticsearch.gchange.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.core.client.model.local.Peer;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.service.CurrencyService;
import org.duniter.elasticsearch.threadpool.ScheduledActionFuture;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.common.inject.Inject;

import java.io.Closeable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class NetworkService extends AbstractService {

    private final org.duniter.elasticsearch.service.NetworkService delegate;
    private final CurrencyService currencyService;
    private final PeerService peerService;
    private final ThreadPool threadPool;

    @Inject
    public NetworkService(Duniter4jClient client, PluginSettings settings, ThreadPool threadPool,
                          CryptoService cryptoService,
                          CurrencyService currencyService,
                          PeerService peerService,
                          org.duniter.elasticsearch.service.NetworkService networkService) {

        super("gchange.network", client, settings, cryptoService);
        this.threadPool = threadPool;
        this.currencyService = currencyService;
        this.peerService = peerService;
        this.delegate = networkService;

        // Register GCHANGE_API as published API, inside the peering document
        delegate.registerPeeringPublishApi(EndpointApi.GCHANGE_API.name());

        // Register GCHANGE_API as target API, for peering document
        delegate.registerPeeringTargetApi(EndpointApi.GCHANGE_API.name());
    }

    public Collection<Peer> getPeersWithGchangeApi() {

        Collection<Peer> peers = Lists.newArrayList();
        for (String currency: currencyService.getAllIds()) {
            peers.addAll(delegate.getPeersFromApi(currency, EndpointApi.GCHANGE_API.name()));
        }

        return peers;
    }

    public Collection<Peer> getConfigPeersWithGchangeApi() {

        Collection<Peer> peers = Lists.newArrayList();
        for (String currency: currencyService.getAllIds()) {
            peers.addAll(delegate.getConfigIncludesPeers(currency, EndpointApi.GCHANGE_API.name()));
        }

        return peers;
    }


    /**
     * Watch network peers, from endpoints found in config
     * @return a tear down logic, to call to stop the listener
     */
    public Optional<Closeable> startListeningPeers() {

        final Collection<Peer> configPeers = getConfigPeersWithGchangeApi();
        if (CollectionUtils.isEmpty(configPeers)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("No compatible endpoints found in config option '%s'. Cannot listening peers from network",
                        "duniter.p2p.includes.endpoints"));
            }
            return Optional.empty();
        }


        // Update peers, using endpoints found in config
        ScheduledActionFuture<?> future = threadPool.scheduleAtFixedRate(() -> {
                // Index config peers
                configPeers.forEach(peerService::indexPeers);
            },
            pluginSettings.getScanPeerInterval(),
            pluginSettings.getScanPeerInterval(),
            TimeUnit.SECONDS);

        // Tear down logic
        return Optional.of(() -> future.cancel(true));
    }

}
