package org.duniter.elasticsearch.gchange.service;

/*
 * #%L
 * Duniter4j :: Core API
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
import org.duniter.core.client.model.ModelUtils;
import org.duniter.core.client.model.bma.BlockchainBlock;
import org.duniter.core.client.model.bma.BlockchainBlocks;
import org.duniter.core.client.model.bma.Constants;
import org.duniter.core.client.model.local.Member;
import org.duniter.core.service.CryptoService;
import org.duniter.core.util.ArrayUtils;
import org.duniter.core.util.CollectionUtils;
import org.duniter.core.util.Preconditions;
import org.duniter.core.util.StringUtils;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.dao.BlockRepository;
import org.duniter.elasticsearch.gchange.model.event.GchangeEventCodes;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.model.user.GchangeUserProfile;
import org.duniter.elasticsearch.model.user.DocumentReference;
import org.duniter.elasticsearch.model.user.UserEvent;
import org.duniter.elasticsearch.service.AbstractBlockchainListenerService;
import org.duniter.elasticsearch.service.WotService;
import org.duniter.elasticsearch.service.changes.ChangeEvent;
import org.duniter.elasticsearch.service.changes.ChangeService;
import org.duniter.elasticsearch.threadpool.ThreadPool;
import org.duniter.elasticsearch.user.PluginSettings;
import org.duniter.elasticsearch.user.dao.profile.UserIndexRepository;
import org.duniter.elasticsearch.user.dao.profile.UserProfileRepository;
import org.duniter.elasticsearch.user.service.UserEventService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.TimeValue;
import org.nuiton.i18n.I18n;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Benoit on 30/03/2015.
 */
public class BlockchainUserEventService extends AbstractBlockchainListenerService  {

    public static final String GCHANGE_PREFIX = "GCHANGE:";
    public static final String LINK_PUBKEY_PREFIX = GCHANGE_PREFIX + "LINK:";
    public static final String UNLINK_PUBKEY_PREFIX = GCHANGE_PREFIX + "UNLINK:";
    public static final String DEFAULT_PUBKEYS_SEPARATOR = ", ";

    private final static Pattern PUBKEY_PATTERN = Pattern.compile("^" + Constants.Regex.PUBKEY + "$");


    private final WotService wotService;
    private final UserEventService userEventService;

    private final MarketService marketService;


    @Inject
    public BlockchainUserEventService(Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService,
                                      ThreadPool threadPool,
                                      WotService wotService,
                                      UserEventService userEventService,
                                      MarketService marketService
                                ) {
        super("duniter.market.event.link", client, pluginSettings.getDelegate(), cryptoService, threadPool,
                new TimeValue(500, TimeUnit.MILLISECONDS));
        this.userEventService = userEventService;
        this.marketService = marketService;
        this.wotService = wotService;

        // Make sure there is no other user event, because "delete bulk will delete own user event !")
        if (pluginSettings.enableBlockchainUserEventIndexation()) {
            logger.warn("Invalid configuration option 'duniter.blockchain.event.user.enable: true': should be set to false for gchange Pod. Cannot index account link");
        }
        else {
            ChangeService.registerListener(this);
        }
    }

    @Override
    protected void processBlockIndex(ChangeEvent change) {
        BlockchainBlock block = readBlock(change);

        // Skip if already processed, or mark as processed (to avoid infinite loop)
        if (isAlreadyIndexed(block)) {
            if (logger.isDebugEnabled()) logger.debug("Skipping already processed block #{}-{}", block.getNumber(), block.getHash());
            return;
        }
        this.markAsIndexed(block);

        processBlock(block);
    }

    @Override
    protected void processBlockDelete(ChangeEvent change) {

        DocumentReference reference = new DocumentReference(change.getIndex(), BlockRepository.TYPE, change.getId());

        if (change.getSource() != null) {
            BlockchainBlock block = readBlock(change);
            reference.setHash(block.getHash());

            // Skip if already deleted, or mark as deleted
            if (isAlreadyDeleted(block)) return;
            this.markAsDeleted(block);
        }

        this.bulkRequest = userEventService.addDeletesByReferenceToBulk(reference, this.bulkRequest, this.bulkSize, false);
        flushBulkRequestOrSchedule();
    }


    /* -- internal method -- */

    protected void processBlock(BlockchainBlock block) {

        // First: Delete old events on same block
        {
            DocumentReference reference = new DocumentReference(block.getCurrency(), BlockRepository.TYPE, String.valueOf(block.getNumber()));
            this.bulkRequest = userEventService.addDeletesByReferenceToBulk(reference, this.bulkRequest, this.bulkSize, false);
            flushBulk();
        }

        // Tx
        if (CollectionUtils.isNotEmpty(block.getTransactions())) {
            for (BlockchainBlock.Transaction tx: block.getTransactions()) {
                processTx(block, tx);
            }
        }

        flushBulkRequestOrSchedule();
    }

    private void processTx(BlockchainBlock block, BlockchainBlock.Transaction tx) {
        if (StringUtils.isBlank(tx.getComment())) return; // Skip if no TX.comment

        // Trim comment
        String comment = tx.getComment().trim();

        // Link
        if (comment.startsWith(LINK_PUBKEY_PREFIX) || comment.startsWith(UNLINK_PUBKEY_PREFIX)) {
            processLinkOrUnlinkPubkey(block, tx, comment);
        }

        else if (comment.startsWith(GCHANGE_PREFIX)) {
            processCrowdfundingTx(block, tx, comment);
        }
    }

    private void processLinkOrUnlinkPubkey(BlockchainBlock block, BlockchainBlock.Transaction tx, String comment) {
        // Remove the prefix, then trim again
        String profilePubkey = comment.startsWith(LINK_PUBKEY_PREFIX)
            ? comment.substring(LINK_PUBKEY_PREFIX.length()).trim()
            : comment.substring(UNLINK_PUBKEY_PREFIX.length()).trim();

        // Extract pubkey (=the first word)
        profilePubkey = profilePubkey.split("\\s+")[0].trim();
        if (!PUBKEY_PATTERN.matcher(profilePubkey).matches()) {
            logger.debug(String.format("Invalid TX with comment '%s': invalid pubkey", comment));
            return;
        }

        // Get the profile to link with
        Optional<GchangeUserProfile> profile = getProfile(profilePubkey);
        if (!profile.isPresent()) {
            logger.debug(String.format("Invalid TX with comment '%s': user profile not found", comment, profilePubkey));
            // Continue anyway (user profiles may be imported later)
        }

        // Extract duniter pubkey, from TX issuer
        if (ArrayUtils.size(tx.getIssuers()) != 1) {
            logger.debug(String.format("Invalid TX with comment '%s': too many issuers", comment));
            return;
        }
        String duniterPubkey = tx.getIssuers()[0];

        // Collect receivers
        Set<String> receivers = new HashSet<>();
        for (String output : tx.getOutputs()) {
            String[] parts = output.split(":");
            if (parts.length >= 3 && parts[2].startsWith("SIG(")) {
                String receiver = parts[2].substring(4, parts[2].length() - 1);
                if (!duniterPubkey.equals(receiver)) {
                    receivers.add(receiver);
                }
            }
        }

        // Check receivers ?


        // Link
        if (comment.startsWith(LINK_PUBKEY_PREFIX)) {
            // Check same pubkey
            if (profile.isPresent() && !StringUtils.equals(profile.get().getPubkey(), duniterPubkey)) {
                logger.debug(String.format("Invalid TX with comment '%s': expected profile's pubkey '%s' not found", comment, duniterPubkey));
                return;
            }

            String duniterUid = wotService.getMemberByPubkey(block.getCurrency(), duniterPubkey)
                .map(Member::getUid)
                .orElse(null);

            // OK: account linked
            notifyUserEvent(block, profilePubkey, profile.map(GchangeUserProfile::getLocale).orElse(null), GchangeEventCodes.LINK_PUBKEY,
                I18n.n("duniter.market.event.LINK_PUBKEY"),
                block.getCurrency(),
                duniterPubkey,
                duniterUid,
                ModelUtils.minifyPubkey(duniterPubkey)
            );

        }

        // Unlink
        else {
            // Check same pubkey
            if (profile.isPresent() && !StringUtils.equals(profile.get().getPubkey(), duniterPubkey)) {
                logger.debug(String.format("Invalid TX with comment '%s': maybe already unlinked", comment));
                return;
            }

            String duniterUid = wotService.getMemberByPubkey(block.getCurrency(), duniterPubkey)
                .map(Member::getUid).orElse(null);

            // OK: account unlinked
            notifyUserEvent(block, profilePubkey, profile.map(GchangeUserProfile::getLocale).orElse(null), GchangeEventCodes.UNLINK_PUBKEY,
                I18n.n("duniter.market.event.UNLINK_PUBKEY"),
                block.getCurrency(),
                duniterPubkey,
                duniterUid,
                ModelUtils.minifyPubkey(duniterPubkey)
            );
        }
    }

    private void processCrowdfundingTx(BlockchainBlock block, BlockchainBlock.Transaction tx, String comment) {
        // Remove the prefix, then trim again
        String recordId = comment.substring(GCHANGE_PREFIX.length()).trim();

        // Get first word => should be the Ad record id
        recordId = recordId.split("\\s+")[0].trim();

        MarketRecord record = marketService.getRecordForEvent(recordId);
        if (record == null) {
            logger.trace(String.format("Invalid %s TX with comment '%s': cannot found ad '%s'",
                MarketRecord.Type.crowdfunding.name(), comment, recordId));
            return;
        }
        if (!MarketRecord.Type.crowdfunding.name().equals(record.getType())) {
            logger.debug(String.format("Invalid %s TX with comment '%s': the market record has an invalid type. Expected '%s' - Actual '%'",
                MarketRecord.Type.crowdfunding.name(), comment,
                MarketRecord.Type.crowdfunding.name(), record.getType()));
            return;
        }

        // Get the record's issuer profile
        GchangeUserProfile profile = getProfile(record.getIssuer()).orElse(null);
        if (profile == null) {
            return; // User not exists: skip
        }
        String recordIssuerDuniterPubkey = profile.getPubkey(); // Account pubkey
        Predicate<String> excludeRecordIssuer = issuer -> issuer != null
            // Exclude record issuer
            && !record.getIssuer().equals(issuer)
            // Exclude record issuer's pubkey
            && (recordIssuerDuniterPubkey == null || recordIssuerDuniterPubkey.equals(issuer));

        long amount = BlockchainBlocks.getTxAmount(tx, excludeRecordIssuer);
        if (amount <= 0) {
            logger.debug(String.format("Invalid %s TX with comment '%s': zero amount",
                MarketRecord.Type.crowdfunding.name(),
                comment));
            return;
        }

        notifyUserEvent(block, record.getIssuer(), profile.getLocale(), GchangeEventCodes.CROWDFUNDING_TX_RECEIVED,
            I18n.n("duniter.market.event.CROWDFUNDING_TX_RECEIVED"),
            tx.getCurrency(),
            Long.toString(amount),
            recordId,
            record.getTitle()
        );
    }

    private void notifyUserEvent(BlockchainBlock block, String pubkey,
                                 @Nullable String locale,
                                 GchangeEventCodes code,
                                 String message,
                                 String... params) {
        Preconditions.checkNotNull(block);
        Preconditions.checkNotNull(pubkey);
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(code);

        UserEvent event = UserEvent.builder(UserEvent.EventType.INFO, code.name())
                .setRecipient(pubkey)
                .setMessage(message, params)
                .setTime(block.getMedianTime())
                .setReference(block.getCurrency(), BlockRepository.TYPE, String.valueOf(block.getNumber()))
                .setReferenceHash(block.getHash())
                .build();

        event = locale == null
            ? userEventService.fillUserEvent(event)
            : userEventService.fillUserEvent(new Locale(locale), event) ;

        try {
            bulkRequest.add(client.prepareIndex(UserEventService.INDEX, UserEventService.EVENT_TYPE)
                    .setSource(getObjectMapper().writeValueAsBytes(event))
                    .setRefresh(false));

            // Flush if need
            if (bulkRequest.numberOfActions() % bulkSize == 0) {
                flushBulk();
            }
        }
        catch(JsonProcessingException e) {
            logger.error("Could not serialize UserEvent into JSON: " + e.getMessage(), e);
        }
    }

    public Optional<GchangeUserProfile> getProfile(String issuer) {
        Preconditions.checkNotNull(issuer);
        GchangeUserProfile result = client.getSourceById(UserIndexRepository.INDEX, UserProfileRepository.TYPE,
            issuer,
            GchangeUserProfile.class,
            GchangeUserProfile.Fields.TITLE,
            GchangeUserProfile.Fields.LOCALE,
            GchangeUserProfile.Fields.ISSUER,
            GchangeUserProfile.Fields.PUBKEY
        );
        return Optional.ofNullable(result);
    }

    public Optional<String> getProfilePubkey(String id) {
        Object result = client.getFieldById(UserIndexRepository.INDEX, UserProfileRepository.TYPE,
            id, GchangeUserProfile.Fields.PUBKEY);
        if (result == null) return Optional.empty();
        return Optional.of(result.toString());
    }
}
