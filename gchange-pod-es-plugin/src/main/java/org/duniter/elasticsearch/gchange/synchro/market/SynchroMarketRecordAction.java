package org.duniter.elasticsearch.gchange.synchro.market;

import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroAction;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.elasticsearch.common.inject.Inject;

public class SynchroMarketRecordAction extends AbstractSynchroAction {

    @Inject
    public SynchroMarketRecordAction(Duniter4jClient client,
                                     PluginSettings pluginSettings,
                                     CryptoService cryptoService,
                                     ThreadPool threadPool,
                                     SynchroService synchroService) {
        super(MarketIndexDao.INDEX, MarketRecordDao.TYPE, client, pluginSettings.getDelegate(), cryptoService, threadPool);

        setEnableUpdate(true); // with update

        synchroService.register(this);
    }

}
