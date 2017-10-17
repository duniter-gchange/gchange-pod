package org.duniter.elasticsearch.gchange.synchro.registry;

import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryCommentDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryIndexDao;
import org.duniter.elasticsearch.synchro.AbstractSynchroAction;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.elasticsearch.common.inject.Inject;

public class SynchroRegistryRecordAction extends AbstractSynchroAction {

    @Inject
    public SynchroRegistryRecordAction(Duniter4jClient client,
                                       PluginSettings pluginSettings,
                                       CryptoService cryptoService,
                                       ThreadPool threadPool,
                                       SynchroService synchroService) {
        super(RegistryIndexDao.INDEX, RegistryCommentDao.TYPE, client, pluginSettings.getDelegate(), cryptoService, threadPool);

        setEnableUpdate(true); // with update

        synchroService.register(this);
    }

}
