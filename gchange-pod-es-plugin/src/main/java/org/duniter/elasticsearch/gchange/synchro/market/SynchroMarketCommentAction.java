package org.duniter.elasticsearch.gchange.synchro.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.model.user.RecordComment;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.duniter.elasticsearch.user.execption.UserProfileNotFoundException;
import org.elasticsearch.common.inject.Inject;

public class SynchroMarketCommentAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            // Medium priority
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            // and AFTER market records
            SynchroMarketRecordAction.EXECUTION_ORDER + 1);

    @Inject
    public SynchroMarketCommentAction(Duniter4jClient client,
                                      PluginSettings pluginSettings,
                                      CryptoService cryptoService,
                                      ThreadPool threadPool,
                                      SynchroService synchroService) {
        super(MarketIndexRepository.INDEX, MarketCommentRepository.TYPE, client, pluginSettings.getDelegate(), cryptoService, threadPool);

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }

    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String issuer = source.get(RecordComment.Fields.ISSUER).asText();

        // Check issuer has a user profile
        if (!hasUserProfile(issuer)) {
            throw new UserProfileNotFoundException(String.format("Unknown market record issuer {%.8s} (profile not exists).", issuer));
        }
    }
}
