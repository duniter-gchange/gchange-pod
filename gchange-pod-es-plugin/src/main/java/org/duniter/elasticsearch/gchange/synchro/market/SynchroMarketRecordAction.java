package org.duniter.elasticsearch.gchange.synchro.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.client.repositories.CurrencyRepository;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.ArrayUtils;
import org.duniter.core.util.Beans;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.dao.CurrencyExtendRepository;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordRepository;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.execption.UserProfileNotFoundException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class SynchroMarketRecordAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            // Medium priority
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            // and AFTER market categories
            SynchroMarketCategoryAction.EXECUTION_ORDER + 1);

    private CurrencyExtendRepository currencyExtendRepository;

    private PluginSettings gchangeSettings;

    @Inject
    public SynchroMarketRecordAction(Duniter4jClient client,
                                     PluginSettings pluginSettings,
                                     CryptoService cryptoService,
                                     ThreadPool threadPool,
                                     SynchroService synchroService,
                                     CurrencyRepository currencyRepository) {
        super(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, client, pluginSettings.getDelegate().getDelegate(), cryptoService, threadPool);

        this.gchangeSettings = pluginSettings;
        this.currencyExtendRepository = (CurrencyExtendRepository) currencyRepository;

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }

    @Override
    protected QueryBuilder createQuery(long fromTime) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                // Get last marker record
                .filter(QueryBuilders.rangeQuery(MarketRecord.Fields.TIME).gte(fromTime));

        // Exclude closed (no stock) Ad
        if (!gchangeSettings.synchronizeClosedAds()) {
            boolQuery.filter(QueryBuilders.rangeQuery(MarketRecord.Fields.STOCK).gte(1));
        }

        String[] currencyIds = Beans.toArray(currencyExtendRepository.findAllIds(), String[]::new);
        if (ArrayUtils.isNotEmpty(currencyIds)) {
            boolQuery.filter(QueryBuilders.termsQuery(MarketRecord.Fields.CURRENCY, currencyIds));
        }

        // Dont care about the score
        return QueryBuilders.constantScoreQuery(boolQuery);
    }

    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String issuer = source.get(MarketRecord.Fields.ISSUER).asText();

        // Check issuer has a user profile
        if (!hasUserProfile(issuer)) {
            throw new UserProfileNotFoundException(String.format("Unknown market record issuer {%.8s} (profile not exists).", issuer));
        }
    }
}
