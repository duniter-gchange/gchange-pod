package org.duniter.elasticsearch.gchange.synchro;

import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.PluginSettings;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.threadpool.ThreadPool;

public abstract class AbstractSynchroAction extends org.duniter.elasticsearch.synchro.AbstractSynchroAction {

    public AbstractSynchroAction(String index, String type, Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService, ThreadPool threadPool) {
        super(index, type, client, pluginSettings, cryptoService, threadPool);
    }

    public AbstractSynchroAction(String fromIndex, String fromType, String toIndex, String toType, Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService, ThreadPool threadPool) {
        super(fromIndex, fromType, toIndex, toType, client, pluginSettings, cryptoService, threadPool);
    }

    // FIXME: allow to extend Duniter4j synchro
    /*@Override
    public String getEndPointApi() {
        return Protocol.GCHANGE_API;
    }*/
}
