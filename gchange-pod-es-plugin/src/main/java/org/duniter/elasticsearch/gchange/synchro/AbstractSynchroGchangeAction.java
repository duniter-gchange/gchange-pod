package org.duniter.elasticsearch.gchange.synchro;

import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.PluginSettings;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.service.UserService;

public abstract class AbstractSynchroGchangeAction extends org.duniter.elasticsearch.synchro.AbstractSynchroAction {

    public AbstractSynchroGchangeAction(String index, String type, Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService, ThreadPool threadPool) {
        super(index, type, client, pluginSettings, cryptoService, threadPool);
    }

    public AbstractSynchroGchangeAction(String fromIndex, String fromType, String toIndex, String toType, Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService, ThreadPool threadPool) {
        super(fromIndex, fromType, toIndex, toType, client, pluginSettings, cryptoService, threadPool);
    }

    @Override
    public String getEndPointApi() {
        return EndpointApi.GCHANGE_API.name();
    }

    /* -- internal methods -- */

    protected boolean hasUserProfile(final String pubkey) {
        return client.isDocumentExists(UserService.INDEX, UserService.PROFILE_TYPE, pubkey);
    }
}
