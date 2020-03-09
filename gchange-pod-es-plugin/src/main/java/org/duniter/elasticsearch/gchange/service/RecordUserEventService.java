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
import org.duniter.core.client.model.ModelUtils;
import org.duniter.core.client.model.bma.jackson.JacksonUtils;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.CollectionUtils;
import org.duniter.core.util.Preconditions;
import org.duniter.core.util.StringUtils;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.model.TitleRecord;
import org.duniter.elasticsearch.gchange.model.event.GchangeEventCodes;
import org.duniter.elasticsearch.gchange.model.market.LightMarketRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.service.changes.ChangeEvent;
import org.duniter.elasticsearch.service.changes.ChangeService;
import org.duniter.elasticsearch.service.changes.ChangeSource;
import org.duniter.elasticsearch.user.dao.page.PageIndexDao;
import org.duniter.elasticsearch.user.dao.page.PageRecordDao;
import org.duniter.elasticsearch.user.dao.profile.UserIndexDao;
import org.duniter.elasticsearch.user.dao.profile.UserProfileDao;
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
public class RecordUserEventService extends AbstractService implements ChangeService.ChangeListener {

    static {
        I18n.n("duniter.market.event.FOLLOW_NEW");
        I18n.n("duniter.market.event.FOLLOW_UPDATE");
        I18n.n("duniter.market.event.FOLLOW_CLOSE");

        I18n.n("duniter.page.event.FOLLOW_NEW");
        I18n.n("duniter.page.event.FOLLOW_UPDATE");

        I18n.n("duniter.market.record.the");
    }

    private final UserService userService;
    private final UserEventService userEventService;
    private final LikeService likeService;
    private final List<ChangeSource> changeListenSources;
    private final boolean trace;
    private ObjectMapper objectMapper;

    @Inject
    public RecordUserEventService(Duniter4jClient client,
                                  PluginSettings settings,
                                  CryptoService cryptoService,
                                  UserService userService,
                                  UserEventService userEventService,
                                  LikeService likeService) {
        super("gchange.event.record", client, settings, cryptoService);
        this.userService = userService;
        this.userEventService = userEventService;
        this.likeService = likeService;
        objectMapper = JacksonUtils.newObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.changeListenSources = ImmutableList.of(
                new ChangeSource(MarketIndexDao.INDEX, MarketRecordDao.TYPE),
                new ChangeSource(PageIndexDao.INDEX, PageRecordDao.TYPE));
        ChangeService.registerListener(this);

        this.trace = logger.isTraceEnabled();
    }

    @Override
    public String getId() {
        return "gchange.event.record";
    }

    @Override
    public void onChange(ChangeEvent change) {

        switch (change.getOperation()) {
            case CREATE:
                readSourceAsRecord(change)
                        .ifPresent(record -> processCreateRecord(change.getIndex(), change.getType(), change.getId(), record));
                break;

            case INDEX:
                readSourceAsRecord(change)
                        .ifPresent(record -> processUpdateRecord(change.getIndex(), change.getType(), change.getId(), record));
                break;

            // on DELETE : remove user event on block (using link
            case DELETE:
                processRecordDelete(change);
                break;
        }

    }

    @Override
    public Collection<ChangeSource> getChangeSources() {
        return changeListenSources;
    }

    /* -- internal method -- */

    /**
     * Send notification from a new record
     *
     * @param index
     * @param type
     * @param recordId
     * @param record
     */
    private void processCreateRecord(String index, String type, String recordId, TitleRecord record) {

        // Fetch record info
        String issuer = record.getIssuer();
        String title = record.getTitle();
        long creationTime = record.getCreationTime();
        String issuerName = userService.getProfileTitle(issuer).orElse(ModelUtils.minifyPubkey(issuer));

        // Get the issuer's followers
        // WARN: we use the issuer, and not market record, because it doesnt exists yet
        Set<String> followers = getFollowers(UserIndexDao.INDEX, UserProfileDao.TYPE, issuer);

        // Exclude the issuer from followers (not need to notify himself)
        followers.remove(issuer);

        // Notify all followers
        if (CollectionUtils.isNotEmpty(followers)) {
            String messageKey = String.format("duniter.%s.event.%s", index.toLowerCase(), GchangeEventCodes.FOLLOW_NEW.name());
            followers.forEach(follower -> {
                userEventService.notifyUser(
                        UserEvent.newBuilder(UserEvent.EventType.INFO, GchangeEventCodes.FOLLOW_NEW.name())
                                .setMessage(
                                        messageKey,
                                        issuer,
                                        issuerName,
                                        title
                                )
                                .setRecipient(follower)
                                .setReference(index, type, recordId)
                                .setTime(creationTime)
                                .build());
            });
        }
    }

    /**
     * Same as processCreateComment(), but with other code and message.
     *
     * @param index
     * @param type
     * @param recordId
     * @param record
     */
    private void processUpdateRecord(String index, String type, String recordId, TitleRecord record) {
        // Fetch record info
        String issuer = record.getIssuer();
        String title = record.getTitle();
        long creationTime = record.getCreationTime();
        long time = record.getTime();

        // Check is Ad now closed
        boolean isClosed = getStock(record).map(stock -> stock == 0).orElse(false);

        // If NOT closed event, and update just after creation: omit event
        if (!isClosed && (time - creationTime) < 3600) return;

        String issuerName = userService.getProfileTitle(issuer).orElse(ModelUtils.minifyPubkey(issuer));

        // Get the record's followers
        Set<String> followers = getFollowers(index, type, recordId);

        // Exclude the record issuer from the follower (already notify, with another message)
        followers.remove(issuer);

        // Notify all followers
        if (CollectionUtils.isNotEmpty(followers)) {
            GchangeEventCodes eventCode =  isClosed ? GchangeEventCodes.FOLLOW_CLOSE : GchangeEventCodes.FOLLOW_UPDATE;
            String messageKey = String.format("duniter.%s.event.%s", index.toLowerCase(), eventCode.name());

            final UserEvent.Builder followerEventBuilder = UserEvent.newBuilder(UserEvent.EventType.INFO, eventCode.name())
                    .setMessage(
                            messageKey,
                            issuer,
                            issuerName,
                            title
                    )
                    .setReference(index, type, recordId)
                    .setTime(time);

            followers.forEach(follower -> userEventService.notifyUser(followerEventBuilder
                                .setRecipient(follower)
                                .build()));
        }
    }

    private void processRecordDelete(ChangeEvent change) {
        if (change.getId() == null) return;

        // Delete events that reference this document
        userEventService.deleteAllByReference(new DocumentReference(change.getIndex(), change.getType(), change.getId()));

        // Delete likes that reference this document
        likeService.deleteAllByReference(new DocumentReference(change.getIndex(), change.getType(), change.getId()));
    }

    private Optional<TitleRecord> readSourceAsRecord(ChangeEvent change) {
        if (change.getSource() == null) return Optional.empty();
        try {
            // Use a light market record, if possible
            if (MarketIndexDao.INDEX.equals(change.getIndex())) {
                return Optional.of(objectMapper.readValue(change.getSource().streamInput(), LightMarketRecord.class));
            }
            else {
                return Optional.of(objectMapper.readValue(change.getSource().streamInput(), TitleRecord.class));
            }
        } catch (JsonProcessingException e) {
            if (trace) {
                logger.warn(String.format("Bad format for comment [%s]: %s. Skip this comment", change.getId(), e.getMessage()), e);
            }
            else {
                logger.warn(String.format("Bad format for comment [%s]: %s. Skip this comment", change.getId(), e.getMessage()));
            }
            return Optional.empty();
        }
        catch (IOException e) {
            throw new TechnicalException(String.format("Unable to parse received comment %s", change.getId()), e);
        }
    }

    public Set<String> getFollowers(String index, String type, String docId) {
        return likeService.getIssuersByDocumentAndKind(index, type, docId, LikeRecord.Kind.FOLLOW);
    }

    protected Optional<Integer> getStock(TitleRecord record) {
        if (record instanceof LightMarketRecord) {
            LightMarketRecord marketRecord = (LightMarketRecord)record;
            return Optional.ofNullable(marketRecord.getStock());
        }

        // No stock in other record (e.g. page)
        return Optional.empty();
    }
}
