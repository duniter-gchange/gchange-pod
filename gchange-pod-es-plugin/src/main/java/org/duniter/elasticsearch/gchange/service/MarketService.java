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
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.ArrayUtils;
import org.duniter.core.util.Preconditions;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.exception.NotFoundException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketCategoryRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordRepository;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecordFilter;
import org.duniter.elasticsearch.model.Record;
import org.duniter.elasticsearch.model.user.RecordComment;
import org.duniter.elasticsearch.user.service.DeleteHistoryService;
import org.elasticsearch.common.inject.Inject;

import java.util.List;

/**
 * Created by Benoit on 30/03/2015.
 */
public class MarketService extends AbstractService {

    private MarketIndexRepository indexRepository;
    private MarketRecordRepository recordRepository;
    private MarketCommentRepository commentRepository;
    private MarketCategoryRepository categoryRepository;
    private DeleteHistoryService deleteHistoryService;

    @Inject
    public MarketService(Duniter4jClient client, PluginSettings settings,
                         CryptoService cryptoService,
                         DeleteHistoryService deleteHistoryService,
                         MarketIndexRepository indexRepository,
                         MarketCommentRepository commentRepository,
                         MarketRecordRepository recordRepository,
                         MarketCategoryRepository categoryRepository
                         ) {
        super("gchange.market", client, settings, cryptoService);
        this.indexRepository = indexRepository;
        this.commentRepository = commentRepository;
        this.recordRepository = recordRepository;
        this.categoryRepository = categoryRepository;

        this.deleteHistoryService = deleteHistoryService;

        setIsReady(true);
    }


    /**
     * Create index need for blockchain registry, if need
     */
    public MarketService createIndexIfNotExists() {
        indexRepository.createIndexIfNotExists();

        return this;
    }

    public MarketService deleteIndex() {
        indexRepository.deleteIndex();
        return this;
    }


    public String indexRecordFromJson(String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", recordRepository.getType(), issuer.substring(0, 8)));
        }

        return recordRepository.create(json);
    }

    public void updateRecordFromJson(String id, String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        // Check same document issuer
        recordRepository.checkSameDocumentIssuer(id, issuer);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating %s [%s] from issuer [%s]", recordRepository.getType(), id, issuer.substring(0, 8)));
        }

        recordRepository.update(id, json);
    }

    public void updateCategoryFromJson(String id, String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        // Check issuer is authorized
        checkIssuerIsAdminOrModerator(issuer);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating %s [%s] from issuer [%s]", MarketIndexRepository.CATEGORY_TYPE, id, issuer.substring(0, 8)));
        }

        categoryRepository.update(id, json);
    }

    public String indexCategoryFromJson(String json) {
        JsonNode categoryObj = readAndVerifyIssuerSignature(json);
        String issuer = getMandatoryField(categoryObj, Record.Fields.ISSUER).asText();

        // Check issuer is authorized
        checkIssuerIsAdminOrModerator(issuer);

        // Check id is unique
        String id = getMandatoryField(categoryObj, "id").asText();
        checkCategoryNotExistsOrDeleted(id);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", MarketIndexRepository.CATEGORY_TYPE, issuer.substring(0, 8)));
        }
        return categoryRepository.create(id, json);
    }

    public String indexCommentFromJson(String json) {
        JsonNode commentObj = readAndVerifyIssuerSignature(json);
        String issuer = getMandatoryField(commentObj, RecordComment.Fields.ISSUER).asText();

        // Check the record document exists
        String recordId = getMandatoryField(commentObj, RecordComment.Fields.RECORD).asText();
        checkRecordExistsOrDeleted(recordId);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", commentRepository.getType(), issuer.substring(0, 8)));
        }
        return commentRepository.create(json);
    }

    public void updateCommentFromJson(String id, String json) {
        JsonNode commentObj = readAndVerifyIssuerSignature(json);

        // Check the record document exists
        String recordId = getMandatoryField(commentObj, RecordComment.Fields.RECORD).asText();
        checkRecordExistsOrDeleted(recordId);

        if (logger.isDebugEnabled()) {
            String issuer = getMandatoryField(commentObj, RecordComment.Fields.ISSUER).asText();
            logger.debug(String.format("[%s] Indexing a %s from issuer [%s] on [%s]", commentRepository.getType(), commentRepository.getType(), issuer.substring(0, 8)));
        }

        commentRepository.update(id, json);
    }


    public MarketRecord getRecordForSharing(String id) {

        return client.getSourceByIdOrNull(recordRepository.getIndex(), recordRepository.getType(), id, MarketRecord.class,
                MarketRecord.Fields.TITLE,
                MarketRecord.Fields.DESCRIPTION,
                MarketRecord.Fields.THUMBNAIL_WITH_CONTENT_TYPE,
                MarketRecord.Fields.PRICE,
                MarketRecord.Fields.CURRENCY,
                MarketRecord.Fields.UNIT);
    }

    public MarketService startDataMigration() {

        // Start index data migration
        indexRepository.startDataMigration();

        return this;
    }

    public <C> List<C> findByFilter(MarketRecordFilter filter, Class<? extends C> clazz) {
        Preconditions.checkNotNull(filter);
        Preconditions.checkArgument(filter.getSize() <= 1000, "Cannot load more than 1000 record per request");

        String[] fieldNames = filter.getFieldNames();
        if (ArrayUtils.isEmpty(fieldNames)) {
            fieldNames = new String[] {
                    MarketRecord.Fields.TYPE,
                    MarketRecord.Fields.CATEGORY,
                    MarketRecord.Fields.TITLE,
                    MarketRecord.Fields.THUMBNAIL_WITH_CONTENT_TYPE,
                    MarketRecord.Fields.PRICE,
                    MarketRecord.Fields.CURRENCY,
                    MarketRecord.Fields.UNIT
            };
        }
        // Replace shape with polygon
        if (filter.getGeoShapeId() != null) {
            // TODO load polygon from the shape id
        }

        return recordRepository.findByFilter(filter, clazz, fieldNames);
    }

    /* -- Internal methods -- */

    // Check the record document exists (or has been deleted)
    private void checkRecordExistsOrDeleted(String id) {
        boolean recordExists;
        try {
            recordExists = recordRepository.existsById(id);
        } catch (NotFoundException e) {
            // Check if exists in delete history
            recordExists = deleteHistoryService.existsInDeleteHistory(recordRepository.getIndex(), recordRepository.getType(), id);
        }
        if (!recordExists) {
            throw new NotFoundException(String.format("Comment refers a non-existent document [%s/%s/%s].", recordRepository.getIndex(), recordRepository.getType(), id));
        }
    }

    // Check the record document exists (or has been deleted)
    private void checkCategoryNotExistsOrDeleted(String id) {
        checkNotExistsOrDeleted(deleteHistoryService, MarketIndexRepository.INDEX, MarketIndexRepository.CATEGORY_TYPE, id);
    }
}
