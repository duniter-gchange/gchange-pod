package org.duniter.elasticsearch.gchange.synchro.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.client.dao.CurrencyDao;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.CollectionUtils;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.dao.CurrencyExtendDao;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketCategoryDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.model.market.MarketCategoryRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.execption.UserProfileNotFoundException;
import org.duniter.elasticsearch.user.synchro.user.SynchroUserProfileAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.duniter.elasticsearch.exception.AccessDeniedException;

import java.util.Set;

public class SynchroMarketCategoryAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            // Medium priority
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            // After user profile
            SynchroUserProfileAction.EXECUTION_ORDER + 1
    );


    @Inject
    public SynchroMarketCategoryAction(Duniter4jClient client,
                                       PluginSettings pluginSettings,
                                       CryptoService cryptoService,
                                       ThreadPool threadPool,
                                       SynchroService synchroService) {
        super(MarketIndexDao.INDEX, MarketCategoryDao.TYPE, client, pluginSettings.getDelegate().getDelegate(), cryptoService, threadPool);

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }

    @Override
    protected QueryBuilder createQuery(long fromTime) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                // Ignore old category (without time, hash, etc)
                .filter(QueryBuilders.existsQuery(MarketCategoryRecord.PROPERTY_TIME))
                // Get last market category
                .filter(QueryBuilders.rangeQuery(MarketCategoryRecord.PROPERTY_TIME).gte(fromTime))
                ;

        // Dont care about the score
        return QueryBuilders.constantScoreQuery(boolQuery);
    }

    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String issuer = source.get(MarketRecord.PROPERTY_ISSUER).asText();

        // Check issuer has a user profile
        checkIssuerIsAdminOrModerator(issuer);
    }

    private void checkIssuerIsAdminOrModerator(String issuer) {
        // Check issuer is an admin
        Set<String> adminAndModeratorsPubkeys = pluginSettings.getDocumentAdminAndModeratorsPubkeys();
        if (!adminAndModeratorsPubkeys.contains(issuer)) {
            throw new AccessDeniedException("Not authorized");
        }
    }
}
