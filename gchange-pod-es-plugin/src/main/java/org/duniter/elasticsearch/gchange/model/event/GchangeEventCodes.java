package org.duniter.elasticsearch.gchange.model.event;

/*
 * #%L
 * Duniter4j :: ElasticSearch GChange plugin
 * %%
 * Copyright (C) 2014 - 2017 EIS
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

/**
 * Created by blavenie on 01/12/16.
 */
public enum GchangeEventCodes {

    NEW_COMMENT,
    UPDATE_COMMENT,
    NEW_REPLY_COMMENT,
    UPDATE_REPLY_COMMENT,

    FOLLOW_NEW_COMMENT,
    FOLLOW_UPDATE_COMMENT,

    FOLLOW_NEW,
    FOLLOW_UPDATE,
    FOLLOW_CLOSE,

    // Linked currency pubkey
    LINK_PUBKEY,
    UNLINK_PUBKEY,
    CROWDFUNDING_TX_RECEIVED
}
