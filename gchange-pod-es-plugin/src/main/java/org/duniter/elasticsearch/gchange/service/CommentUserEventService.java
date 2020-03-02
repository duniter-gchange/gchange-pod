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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.MapUtils;
import org.duniter.core.client.model.ModelUtils;
import org.duniter.core.client.model.bma.jackson.JacksonUtils;
import org.duniter.core.client.model.elasticsearch.RecordComment;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.CollectionUtils;
import org.duniter.core.util.Preconditions;
import org.duniter.core.util.StringUtils;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.RecordDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryCommentDao;
import org.duniter.elasticsearch.gchange.dao.registry.RegistryIndexDao;
import org.duniter.elasticsearch.gchange.model.event.GchangeEventCodes;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.service.changes.ChangeEvent;
import org.duniter.elasticsearch.service.changes.ChangeService;
import org.duniter.elasticsearch.service.changes.ChangeSource;
import org.duniter.elasticsearch.user.model.DocumentReference;
import org.duniter.elasticsearch.user.model.LikeRecord;
import org.duniter.elasticsearch.user.model.UserEvent;
import org.duniter.elasticsearch.user.model.UserProfile;
import org.duniter.elasticsearch.user.service.LikeService;
import org.duniter.elasticsearch.user.service.UserEventService;
import org.duniter.elasticsearch.user.service.UserService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.nuiton.i18n.I18n;

import java.io.IOException;
import java.util.*;

/**
 * Created by Benoit on 30/03/2015.
 */
public class CommentUserEventService extends AbstractService implements ChangeService.ChangeListener {

    static {
        I18n.n("duniter.market.error.comment.recordNotFound");
        I18n.n("duniter.market.event.NEW_COMMENT");
        I18n.n("duniter.market.event.UPDATE_COMMENT");
        I18n.n("duniter.market.event.NEW_REPLY_COMMENT");
        I18n.n("duniter.market.event.UPDATE_REPLY_COMMENT");
        I18n.n("duniter.market.event.FOLLOW_NEW_COMMENT");
        I18n.n("duniter.market.event.FOLLOW_UPDATE_COMMENT");

        I18n.n("duniter.registry.error.comment.recordNotFound");
        I18n.n("duniter.registry.event.NEW_COMMENT");
        I18n.n("duniter.registry.event.UPDATE_COMMENT");
        I18n.n("duniter.registry.event.NEW_REPLY_COMMENT");
        I18n.n("duniter.registry.event.UPDATE_REPLY_COMMENT");
        I18n.n("duniter.registry.event.FOLLOW_NEW_COMMENT");
        I18n.n("duniter.registry.event.FOLLOW_UPDATE_COMMENT");
    }

    public static final UserProfile EMPTY_PROFILE = new UserProfile();

    private final UserService userService;
    private final UserEventService userEventService;
    private final List<ChangeSource> changeListenSources;
    private final String recordType;
    private final boolean trace;
    private ObjectMapper objectMapper;

    @Inject
    public CommentUserEventService(Duniter4jClient client,
                                   PluginSettings settings,
                                   CryptoService cryptoService,
                                   UserService userService,
                                   UserEventService userEventService) {
        super("gchange.event.comment", client, settings, cryptoService);
        this.userService = userService;
        this.userEventService = userEventService;
        objectMapper = JacksonUtils.newObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.changeListenSources = ImmutableList.of(
                new ChangeSource(MarketIndexDao.INDEX, MarketCommentDao.TYPE),
                new ChangeSource(RegistryIndexDao.INDEX, RegistryCommentDao.TYPE));
        ChangeService.registerListener(this);

        this.trace = logger.isTraceEnabled();

        this.recordType = RecordDao.TYPE;
    }

    @Override
    public String getId() {
        return "gchange.event.comment";
    }

    @Override
    public void onChange(ChangeEvent change) {

        RecordComment comment;

        switch (change.getOperation()) {
            case CREATE:
                comment = readComment(change);
                if (comment != null) {
                    processCreateComment(change.getIndex(), change.getType(), change.getId(), comment);
                }
                break;
            case INDEX:
                comment = readComment(change);
                if (comment != null) {
                    processUpdateComment(change.getIndex(), change.getType(), change.getId(), comment);
                }
                break;

            // on DELETE : remove user event on block (using link
            case DELETE:
                processCommentDelete(change);
                break;
        }

    }

    @Override
    public Collection<ChangeSource> getChangeSources() {
        return changeListenSources;
    }

    /* -- internal method -- */

    /**
     * Send notification from a new comment
     *
     * @param index
     * @param type
     * @param commentId
     * @param comment
     */
    private void processCreateComment(String index, String type, String commentId, RecordComment comment) {

        processUpdateOrCreateComment(index, type, commentId, comment,
                GchangeEventCodes.NEW_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.NEW_COMMENT.name()),
                GchangeEventCodes.NEW_REPLY_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.NEW_REPLY_COMMENT.name()),
                GchangeEventCodes.FOLLOW_NEW_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.FOLLOW_NEW_COMMENT.name()));
    }

    /**
     * Same as processCreateComment(), but with other code and message.
     *
     * @param index
     * @param type
     * @param commentId
     * @param comment
     */
    private void processUpdateComment(String index, String type, String commentId, RecordComment comment) {

        processUpdateOrCreateComment(index, type, commentId, comment,
                GchangeEventCodes.UPDATE_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.UPDATE_COMMENT.name()),
                GchangeEventCodes.UPDATE_REPLY_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.UPDATE_REPLY_COMMENT),
                GchangeEventCodes.FOLLOW_UPDATE_COMMENT, String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.FOLLOW_UPDATE_COMMENT)
        );
    }


    /**
     * Same as processCreateComment(), but with other code and message.
     *
     * @param index
     * @param type
     * @param commentId
     * @param comment
     */
    private void processUpdateOrCreateComment(String index, String type, String commentId, RecordComment comment,
                                              GchangeEventCodes eventCodeForRecordIssuer, String messageKeyForRecordIssuer,
                                              GchangeEventCodes eventCodeForParentCommentIssuer, String messageKeyForParentCommentIssuer,
                                              GchangeEventCodes eventCodeForRecordFollowers, String messageKeyForRecordFollowers) {
        // Get record issuer
        String recordId = comment.getRecord();
        Map<String, Object> record = client.getFieldsById(index, this.recordType, recordId,
                MarketRecord.PROPERTY_TITLE, MarketRecord.PROPERTY_ISSUER);

        // Record not found : nothing to emit
        if (MapUtils.isEmpty(record)) {
            logger.warn(I18n.t(String.format("duniter.%s.error.comment.recordNotFound", index.toLowerCase()), recordId));
            return;
        }

        // Notify all followers
        Set<String> followers = getFollowers(index, recordType, recordId);

        // Fetch record info
        String recordIssuer = record.get(MarketRecord.PROPERTY_ISSUER).toString();
        String recordTitle = record.get(MarketRecord.PROPERTY_TITLE).toString();
        UserProfile recordIssuerProfile = userService.getProfileByPubkey(recordIssuer).orElse(EMPTY_PROFILE);

        // Get comment issuer title
        String issuer = comment.getIssuer();
        UserProfile commentIssuerProfile = userService.getProfileByPubkey(issuer).orElse(EMPTY_PROFILE);
        String issuerName = Optional.ofNullable(commentIssuerProfile.getTitle()).orElse(ModelUtils.minifyPubkey(issuer));

        // Notify issuer of record (is not same as comment writer)
        if (!Objects.equals(issuer, recordIssuer)) {
            userEventService.notifyUser(
                    // Record issuer local
                    Optional.ofNullable(recordIssuerProfile.getLocale()).map(Locale::new).orElse(null),
                    UserEvent.newBuilder(UserEvent.EventType.INFO, eventCodeForRecordIssuer.name())
                            .setMessage(
                                    messageKeyForRecordIssuer,
                                    issuer,
                                    issuerName,
                                    recordTitle
                            )
                            .setRecipient(recordIssuer)
                            .setReference(index, recordType, recordId)
                            .setReferenceAnchor(commentId)
                            .setTime(comment.getTime())
                            .build());
        }

        // Notify comment is a reply to another comment
        if (StringUtils.isNotBlank(comment.getReplyTo())) {

            String parentCommentIssuer = client.getTypedFieldById(index, type, comment.getReplyTo(), RecordComment.PROPERTY_ISSUER);

            if (StringUtils.isNotBlank(parentCommentIssuer) &&
                    !issuer.equals(parentCommentIssuer) &&
                    !recordIssuer.equals(parentCommentIssuer)) {

                userEventService.notifyUser(
                        UserEvent.newBuilder(UserEvent.EventType.INFO, eventCodeForParentCommentIssuer.name())
                                .setMessage(
                                        messageKeyForParentCommentIssuer,
                                        issuer,
                                        issuerName,
                                        recordTitle
                                )
                                .setRecipient(parentCommentIssuer)
                                .setReference(index, recordType, recordId)
                                .setReferenceAnchor(commentId)
                                /*.setTime(comment.getTime()) - DO NOT set time, has the comment time is NOT the update time*/
                                .build());

                // Exclude from the followers, as already notify
                followers.remove(parentCommentIssuer);
            }
        }

        // Exclude the record issuer from followers (already notify, with another message)
        followers.remove(recordIssuer);

        // Exclude the comment writer from followers (not need to be notified - fix #6)
        followers.remove(issuer);

        // Notify all followers
        if (CollectionUtils.isNotEmpty(followers)) {
            followers.forEach(follower -> {
                userEventService.notifyUser(
                        UserEvent.newBuilder(UserEvent.EventType.INFO, eventCodeForRecordFollowers.name())
                                .setMessage(
                                        messageKeyForRecordFollowers,
                                        issuer,
                                        issuerName,
                                        recordTitle
                                )
                                .setRecipient(follower)
                                .setReference(index, recordType, recordId)
                                .setReferenceAnchor(commentId)
                                /*.setTime(comment.getTime()) - DO NOT set time, has the comment time is NOT the update time*/
                                .build());
            });
        }

    }

    private void processCommentDelete(ChangeEvent change) {
        if (change.getId() == null) return;

        // Delete events that reference this block
        userEventService.deleteAllByReference(new DocumentReference(change.getIndex(), change.getType(), change.getId()));
    }

    private RecordComment readComment(ChangeEvent change) {
        try {
            if (change.getSource() != null) {
                return objectMapper.readValue(change.getSource().array(), RecordComment.class);
            }
            return null;
        } catch (JsonProcessingException e) {
            if (trace) {
                logger.warn(String.format("Bad format for comment [%s]: %s. Skip this comment", change.getId(), e.getMessage()), e);
            }
            else {
                logger.warn(String.format("Bad format for comment [%s]: %s. Skip this comment", change.getId(), e.getMessage()));
            }
            return null;
        }
        catch (IOException e) {
            throw new TechnicalException(String.format("Unable to parse received comment %s", change.getId()), e);
        }
    }

    public Set<String> getFollowers(String index, String type, String docId) {
        return getIssuersByDocumentAndKind(index, type, docId, LikeRecord.Kind.FOLLOW);
    }

    // TODO: replace with LikeService.getIssuersByDocumentAndKind() (need pod v1.5.16)
    public Set<String> getIssuersByDocumentAndKind(String index, String type, String docId, LikeRecord.Kind kind) {
        Preconditions.checkArgument(StringUtils.isNotBlank(index));
        Preconditions.checkNotNull(StringUtils.isNotBlank(type));
        Preconditions.checkNotNull(StringUtils.isNotBlank(docId));
        Preconditions.checkNotNull(kind);

        int size = 1000;

        // Prepare search request
        SearchRequestBuilder request = client
                .prepareSearch(LikeService.INDEX)
                .setTypes(LikeService.RECORD_TYPE)
                .setFetchSource(false)
                .addFields(LikeRecord.PROPERTY_ISSUER)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setSize(size);

        // Query = filter on index/type/id
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(LikeRecord.PROPERTY_INDEX, index))
                .filter(QueryBuilders.termQuery(LikeRecord.PROPERTY_TYPE, type))
                .filter(QueryBuilders.termQuery(LikeRecord.PROPERTY_ID, docId))
                .filter(QueryBuilders.termQuery(LikeRecord.PROPERTY_KIND, kind.toString()));

        request.setQuery(QueryBuilders.constantScoreQuery(boolQuery));


        // Execute query
        long total = -1;
        int from = 0;
        Set<String> result = Sets.newHashSet();
        do {
            SearchResponse response = client.safeExecuteRequest(request).actionGet();

            // Read query result
            SearchHit[] searchHits = response.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, SearchHitField> hitFields = searchHit.getFields();
                SearchHitField issuerField = hitFields.get(LikeRecord.PROPERTY_ISSUER);
                if (issuerField != null) {
                    result.add(issuerField.getValue());
                }
            }
            from += size;
            request.setFrom(from);
            if (total == -1) total = (response.getHits() != null) ? response.getHits().getTotalHits() : 0;
        } while (from < total);
        return result;
    }
}
