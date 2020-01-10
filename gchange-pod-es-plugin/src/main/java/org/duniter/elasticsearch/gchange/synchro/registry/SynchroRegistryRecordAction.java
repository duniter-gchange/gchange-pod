package org.duniter.elasticsearch.gchange.synchro.registry;

import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryCommentDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryIndexDao;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.duniter.elasticsearch.user.synchro.user.SynchroUserProfileAction;
import org.elasticsearch.common.inject.Inject;

public class SynchroRegistryRecordAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            SynchroUserProfileAction.EXECUTION_ORDER + 1
    );

    @Inject
    public SynchroRegistryRecordAction(Duniter4jClient client,
                                       PluginSettings pluginSettings,
                                       CryptoService cryptoService,
                                       ThreadPool threadPool,
                                       SynchroService synchroService) {
        super(RegistryIndexDao.INDEX, RegistryCommentDao.TYPE, client, pluginSettings.getDelegate(), cryptoService, threadPool);

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        synchroService.register(this);
    }

}
