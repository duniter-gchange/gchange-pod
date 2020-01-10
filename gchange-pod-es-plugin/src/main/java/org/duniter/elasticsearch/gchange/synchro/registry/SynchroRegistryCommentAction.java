package org.duniter.elasticsearch.gchange.synchro.registry;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.client.model.elasticsearch.RecordComment;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryCommentDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryIndexDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryRecordDao;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.gchange.synchro.market.SynchroMarketRecordAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.duniter.elasticsearch.user.dao.group.GroupIndexDao;
import org.duniter.elasticsearch.user.dao.group.GroupRecordDao;
import org.duniter.elasticsearch.user.execption.UserProfileNotFoundException;
import org.duniter.elasticsearch.user.synchro.user.SynchroUserProfileAction;
import org.elasticsearch.common.inject.Inject;

public class SynchroRegistryCommentAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            SynchroRegistryRecordAction.EXECUTION_ORDER + 1
    );

    @Inject
    public SynchroRegistryCommentAction(Duniter4jClient client,
                                        PluginSettings pluginSettings,
                                        CryptoService cryptoService,
                                        ThreadPool threadPool,
                                        SynchroService synchroService) {
        super(RegistryIndexDao.INDEX, RegistryCommentDao.TYPE, client, pluginSettings.getDelegate(), cryptoService, threadPool);

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }


    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String recordId = source.get(RecordComment.PROPERTY_RECORD).asText();

        // Check issuer has a user profile
        if (client.isDocumentExists(RegistryIndexDao.INDEX, RegistryRecordDao.TYPE, recordId)) {
            throw new UserProfileNotFoundException(String.format("Comment on an unknown page {%}.", recordId));
        }
    }

}
