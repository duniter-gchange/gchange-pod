package org.duniter.elasticsearch.gchange.service;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.client.model.elasticsearch.Record;
import org.duniter.core.client.model.elasticsearch.RecordComment;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.exception.NotFoundException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketCategoryDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.user.service.DeleteHistoryService;
import org.elasticsearch.common.inject.Inject;

/**
 * Created by Benoit on 30/03/2015.
 */
public class MarketService extends AbstractService {

    private MarketIndexDao indexDao;
    private MarketRecordDao recordDao;
    private MarketCommentDao commentDao;
    private MarketCategoryDao categoryDao;
    private DeleteHistoryService deleteService;

    @Inject
    public MarketService(Duniter4jClient client, PluginSettings settings,
                         CryptoService cryptoService,
                         DeleteHistoryService deleteService,
                         MarketIndexDao indexDao,
                         MarketCommentDao commentDao,
                         MarketRecordDao recordDao,
                         MarketCategoryDao categoryDao
                         ) {
        super("gchange.market", client, settings, cryptoService);
        this.indexDao = indexDao;
        this.commentDao = commentDao;
        this.recordDao = recordDao;
        this.categoryDao = categoryDao;

        this.deleteService = deleteService;
    }


    /**
     * Create index need for blockchain registry, if need
     */
    public MarketService createIndexIfNotExists() {
        indexDao.createIndexIfNotExists();

        return this;
    }

    public MarketService deleteIndex() {
        indexDao.deleteIndex();
        return this;
    }


    public String indexRecordFromJson(String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", recordDao.getType(), issuer.substring(0, 8)));
        }

        return recordDao.create(json);
    }

    public void updateRecordFromJson(String id, String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        // Check same document issuer
        recordDao.checkSameDocumentIssuer(id, issuer);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating %s [%s] from issuer [%s]", recordDao.getType(), id, issuer.substring(0, 8)));
        }

        recordDao.update(id, json);
    }

    public void updateCategoryFromJson(String id, String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        // Check issuer is authorized
        checkIssuerIsAdminOrModerator(issuer);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating %s [%s] from issuer [%s]", MarketIndexDao.CATEGORY_TYPE, id, issuer.substring(0, 8)));
        }

        categoryDao.update(id, json);
    }

    public String indexCategoryFromJson(String json) {
        JsonNode categoryObj = readAndVerifyIssuerSignature(json);
        String issuer = getMandatoryField(categoryObj, Record.PROPERTY_ISSUER).asText();

        // Check issuer is authorized
        checkIssuerIsAdminOrModerator(issuer);

        // Check id is unique
        String id = getMandatoryField(categoryObj, "id").asText();
        checkCategoryNotExistsOrDeleted(id);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", MarketIndexDao.CATEGORY_TYPE, issuer.substring(0, 8)));
        }
        return categoryDao.create(id, json);
    }

    public String indexCommentFromJson(String json) {
        JsonNode commentObj = readAndVerifyIssuerSignature(json);
        String issuer = getMandatoryField(commentObj, RecordComment.PROPERTY_ISSUER).asText();

        // Check the record document exists
        String recordId = getMandatoryField(commentObj, RecordComment.PROPERTY_RECORD).asText();
        checkRecordExistsOrDeleted(recordId);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", commentDao.getType(), issuer.substring(0, 8)));
        }
        return commentDao.create(json);
    }

    public void updateCommentFromJson(String id, String json) {
        JsonNode commentObj = readAndVerifyIssuerSignature(json);

        // Check the record document exists
        String recordId = getMandatoryField(commentObj, RecordComment.PROPERTY_RECORD).asText();
        checkRecordExistsOrDeleted(recordId);

        if (logger.isDebugEnabled()) {
            String issuer = getMandatoryField(commentObj, RecordComment.PROPERTY_ISSUER).asText();
            logger.debug(String.format("[%s] Indexing a %s from issuer [%s] on [%s]", commentDao.getType(), commentDao.getType(), issuer.substring(0, 8)));
        }

        commentDao.update(id, json);
    }


    public MarketRecord getRecordForSharing(String id) {

        return client.getSourceByIdOrNull(recordDao.getIndex(), recordDao.getType(), id, MarketRecord.class,
                MarketRecord.PROPERTY_TITLE,
                MarketRecord.PROPERTY_DESCRIPTION,
                MarketRecord.PROPERTY_THUMBNAIL,
                MarketRecord.PROPERTY_PRICE,
                MarketRecord.PROPERTY_CURRENCY,
                MarketRecord.PROPERTY_UNIT);
    }

    public MarketService startDataMigration() {

        // Start index data migration
        indexDao.startDataMigration();

        return this;
    }

    /* -- Internal methods -- */

    // Check the record document exists (or has been deleted)
    private void checkRecordExistsOrDeleted(String id) {
        boolean recordExists;
        try {
            recordExists = recordDao.isExists(id);
        } catch (NotFoundException e) {
            // Check if exists in delete history
            recordExists = deleteService.existsInDeleteHistory(recordDao.getIndex(), recordDao.getType(), id);
        }
        if (!recordExists) {
            throw new NotFoundException(String.format("Comment refers a non-existent document [%s/%s/%s].", recordDao.getIndex(), recordDao.getType(), id));
        }
    }

    // Check the record document exists (or has been deleted)
    private void checkCategoryNotExistsOrDeleted(String id) {
        checkNotExistsOrDeleted(deleteService, MarketIndexDao.INDEX, MarketIndexDao.CATEGORY_TYPE, id);
    }
}
