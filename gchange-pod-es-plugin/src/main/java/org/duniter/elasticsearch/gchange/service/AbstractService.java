package org.duniter.elasticsearch.gchange.service;

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

import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.exception.AccessDeniedException;
import org.duniter.elasticsearch.exception.DuplicateIndexIdException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.user.service.DeleteHistoryService;
import org.elasticsearch.client.Client;

import java.util.Set;

/**
 * Created by blavenie on 10/01/17.
 */
public abstract class AbstractService extends org.duniter.elasticsearch.user.service.AbstractService {

    protected PluginSettings pluginSettings;

    public AbstractService(String loggerName, Duniter4jClient client, PluginSettings pluginSettings) {
        this(loggerName, client, pluginSettings, null);
    }

    public AbstractService(Duniter4jClient client, PluginSettings pluginSettings) {
        this(client, pluginSettings, null);
    }

    public AbstractService(Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService) {
        this("duniter.gchange", client, pluginSettings, cryptoService);
    }

    public AbstractService(String loggerName, Duniter4jClient client, PluginSettings pluginSettings, CryptoService cryptoService) {
        super(loggerName, client, pluginSettings.getDelegate(), cryptoService);
        this.pluginSettings = pluginSettings;
    }

    /**
     * Check issuer is an admin
     */
    protected void checkIssuerIsAdminOrModerator(String issuer) {

        Set<String> adminAndModeratorsPubkeys = pluginSettings.getDocumentAdminAndModeratorsPubkeys();
        if (!adminAndModeratorsPubkeys.contains(issuer)) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    /**
     * Check the record document exists (or has been deleted)
     * @param deleteService
     * @param index
     * @param type
     * @param id
     */
    protected void checkNotExistsOrDeleted(DeleteHistoryService deleteService, String index, String type, String id) {
        checkNotExists(index, type, id);

        // Check if exists in delete history
        boolean exists = deleteService.existsInDeleteHistory(index, type, id);

        if (exists) {
            throw new DuplicateIndexIdException(String.format("Category [%s/%s/%s] already exists in the delete history.", index, type, id));
        }
    }

    /**
     * Check the record document exists (or has been deleted)
     * @param deleteService
     * @param index
     * @param type
     * @param id
     */
    protected void checkNotExists(String index, String type, String id) {
        boolean exists = client.isDocumentExists(index, type, id);

        if (exists) {
            throw new DuplicateIndexIdException(String.format("Category [%s/%s/%s] already exists", index, type, id));
        }
    }
}
