package org.duniter.elasticsearch.gchange.synchro.shape;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.exception.AccessDeniedException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.shape.ShapeDao;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.synchro.AbstractSynchroGchangeAction;
import org.duniter.elasticsearch.synchro.SynchroAction;
import org.duniter.elasticsearch.synchro.SynchroActionResult;
import org.duniter.elasticsearch.synchro.SynchroService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.synchro.user.SynchroUserProfileAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Set;

public class SynchroShapeRecordAction extends AbstractSynchroGchangeAction {

    public static final int EXECUTION_ORDER = Math.max(
            // Medium priority
            SynchroAction.EXECUTION_ORDER_MIDDLE,
            // and AFTER market categories
            SynchroUserProfileAction.EXECUTION_ORDER + 1);

    private ShapeDao shapeDao;

    private PluginSettings gchangeSettings;

    private Set<String> adminAndModeratorsPubkeys;

    @Inject
    public SynchroShapeRecordAction(Duniter4jClient client,
                                    PluginSettings pluginSettings,
                                    CryptoService cryptoService,
                                    ThreadPool threadPool,
                                    SynchroService synchroService,
                                    ShapeDao shapeDao) {
        super(ShapeDao.INDEX, ShapeDao.TYPE, client, pluginSettings.getDelegate().getDelegate(), cryptoService, threadPool);

        this.gchangeSettings = pluginSettings;
        this.shapeDao = shapeDao;

        setExecutionOrder(EXECUTION_ORDER);

        setEnableUpdate(true); // with update

        addValidationListener(this::onValidate);

        synchroService.register(this);
    }

    @Override
    protected QueryBuilder createQuery(long fromTime) {

        // Update admin+moderator set
        adminAndModeratorsPubkeys = pluginSettings.getDocumentAdminAndModeratorsPubkeys();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                // Get last marker record
                .filter(QueryBuilders.rangeQuery(MarketRecord.Fields.TIME).gte(fromTime));

        // Dont care about the score
        return QueryBuilders.constantScoreQuery(boolQuery);
    }

    protected void onValidate(String id, JsonNode source, SynchroActionResult result) {

        String issuer = source.get(MarketRecord.Fields.ISSUER).asText();

        // Check issuer is admin or moderator
        checkIssuerIsAdminOrModerator(issuer);
    }

    private void checkIssuerIsAdminOrModerator(String issuer) {
        // Check issuer is an admin or moderator
        if (adminAndModeratorsPubkeys == null || !adminAndModeratorsPubkeys.contains(issuer)) {
            throw new AccessDeniedException(String.format("Unknown shape issuer {%.8s} (not an admin or moderator)", issuer));
        }
    }
}
