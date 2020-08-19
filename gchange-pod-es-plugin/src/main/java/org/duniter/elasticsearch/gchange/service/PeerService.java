package org.duniter.elasticsearch.gchange.service;

import com.google.common.collect.ImmutableList;
import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.core.client.model.local.Peer;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.model.bma.GchangeEndpoindApi;
import org.elasticsearch.common.inject.Inject;
import org.nuiton.i18n.I18n;

public class PeerService extends AbstractService {

    private final org.duniter.elasticsearch.service.PeerService delegate;

    @Inject
    public PeerService(Duniter4jClient client, PluginSettings settings,
                       CryptoService cryptoService,
                       org.duniter.elasticsearch.service.PeerService peerService) {

        super("gchange.peer", client, settings, cryptoService);

        this.delegate = peerService;

        // Retrieve peer with the GCHANGE_API api
        this.delegate.addIndexedEndpointApi(GchangeEndpoindApi.GCHANGE_API.label());
    }

    public String getCurrency(Peer peer) {
        return delegate.getCurrency(peer);
    }


    public PeerService indexPeers(Peer mainPeer) {

        logger.info(I18n.t("duniter.market.peerService.indexPeers.task", mainPeer));

        try {
            String currency = getCurrency(mainPeer);
            org.duniter.core.client.service.local.NetworkService.Filter filterDef = getDefaultFilter(currency);
            delegate.indexPeers(currency, mainPeer, filterDef);
        } catch(Exception e) {
            logger.error("Error during indexPeers: " + e.getMessage(), e);
        }

        return this;
    }

    /* -- protected function -- */

    protected org.duniter.core.client.service.local.NetworkService.Filter getDefaultFilter(String currency) {
        org.duniter.core.client.service.local.NetworkService.Filter filterDef = new org.duniter.core.client.service.local.NetworkService.Filter();
        filterDef.filterType = null;
        filterDef.filterStatus = Peer.PeerStatus.UP;
        // Filter ONLY on GCHANGE_API endpoints
        filterDef.filterEndpoints = ImmutableList.of(GchangeEndpoindApi.GCHANGE_API.label());
        filterDef.currency = currency;
        return filterDef;
    }
}
