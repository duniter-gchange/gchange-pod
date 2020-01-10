package org.duniter.elasticsearch.gchange.synchro.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.client.dao.CurrencyDao;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.CollectionUtils;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.dao.CurrencyExtendDao;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.execption.UserProfileNotFoundException;
import org.duniter.elasticsearch.user.model.Message;
import org.duniter.elasticsearch.user.synchro.user.SynchroUserProfileAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Set;

public class SynchroMarketRecordAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(SynchroUserProfileAction.EXECUTION_ORDER,
            SynchroAction.EXECUTION_ORDER_MIDDLE);

    private CurrencyExtendDao currencyDao;

    private PluginSettings gchangeSettings;

    @Inject
    public SynchroMarketRecordAction(Duniter4jClient client,
                                     PluginSettings pluginSettings,
                                     CryptoService cryptoService,
                                     ThreadPool threadPool,
                                     SynchroService synchroService,
                                     CurrencyDao currencyDao) {
        super(MarketIndexDao.INDEX, MarketRecordDao.TYPE, client, pluginSettings.getDelegate().getDelegate(), cryptoService, threadPool);

        this.gchangeSettings = pluginSettings;
        this.currencyDao = (CurrencyExtendDao)currencyDao;

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }

    @Override
    protected QueryBuilder createQuery(long fromTime) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                // Get last marker record
                .filter(QueryBuilders.rangeQuery(MarketRecord.PROPERTY_TIME).gte(fromTime));

        // Exclude closed (no stock) Ad
        if (!gchangeSettings.synchronizeClosedAds()) {
            boolQuery.filter(QueryBuilders.rangeQuery(MarketRecord.PROPERTY_STOCK).gte(1));
        }

        Set<String> currencyIds = currencyDao.getAllIds();
        if (CollectionUtils.isNotEmpty(currencyIds)) {
            boolQuery.filter(QueryBuilders.termsQuery(MarketRecord.PROPERTY_CURRENCY, currencyIds.toArray(new String[currencyIds.size()])));
        }

        // Dont care about the score
        return QueryBuilders.constantScoreQuery(boolQuery);
    }

    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String issuer = source.get(MarketRecord.PROPERTY_ISSUER).asText();

        // Check issuer has a user profile
        if (!hasUserProfile(issuer)) {
            throw new UserProfileNotFoundException(String.format("Unknown market record issuer {%.8s} (profile not exists).", issuer));
        }
    }
}
