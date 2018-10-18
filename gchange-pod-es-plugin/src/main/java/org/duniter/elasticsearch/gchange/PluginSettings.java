package org.duniter.elasticsearch.gchange;

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


import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.core.util.crypto.KeyPair;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;

import java.util.List;

/**
 * Access to configuration options
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class PluginSettings extends AbstractLifecycleComponent<PluginSettings> {

    private org.duniter.elasticsearch.user.PluginSettings delegate;

    @Inject
    public PluginSettings(org.elasticsearch.common.settings.Settings settings,
                          org.duniter.elasticsearch.user.PluginSettings delegate) {
        super(settings);
        this.delegate = delegate;

        // Add i18n bundle name
        delegate.addI18nBundleName(getI18nBundleName());

    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    protected void doClose() {

    }

    public org.duniter.elasticsearch.user.PluginSettings getDelegate() {
        return delegate;
    }

    public String getGchangeUrl() {
        return this.settings.get("duniter.gchange.url", "https://gchange.fr");
    }

    public String getShareMarketLinkUrl() {
        return this.settings.get("duniter.share.market.link.url", getGchangeUrl() + "/#/app/market/view/{id}/{title}");
    }

    /* -- delegate methods -- */

    public String getNodePubkey() {
        return delegate.getNodePubkey();
    }

    public org.duniter.core.client.model.local.Peer checkAndGetPeer() {
        return delegate.getDelegate().checkAndGetPeer();
    }

    public String getClusterRemoteHost() {
        return delegate.getClusterRemoteHost();
    }

    public int getClusterRemotePort() {
        return delegate.getClusterRemotePort();
    }

    public boolean getClusterRemoteUseSsl() {
        return delegate.getClusterRemoteUseSsl();
    }

    public KeyPair getNodeKeypair() {
        return delegate.getNodeKeypair();
    }

    public String getClusterRemoteUrlOrNull() {
        return delegate.getClusterRemoteUrlOrNull();
    }

    public boolean reloadAllIndices() {
        return delegate.reloadAllIndices();
    }

    public boolean enableSynchro() {
        return delegate.enableSynchro();
    }

    public boolean enablePeering() {
        return this.delegate.enablePeering();
    }

    public List<EndpointApi> getPeeringTargetedApis() {
        return this.delegate.getPeeringTargetedApis();
    }

    public List<EndpointApi> getPeeringPublishedApis() {
        return this.delegate.getPeeringPublishedApis();
    }

    public boolean enableDocStats() {
        return delegate.enableDocStats();
    }

    public boolean getMailEnable() {
        return delegate.getMailEnable();
    }

    public String getMailSmtpHost() {
        return delegate.getMailSmtpHost();
    }

    public int getMailSmtpPort() {
        return delegate.getMailSmtpPort();
    }

    public String getMailSmtpUsername() {
        return delegate.getMailSmtpUsername();
    }

    public String getMailSmtpPassword() {
        return delegate.getMailSmtpPassword();
    }

    public String getMailAdmin() {
        return delegate.getMailAdmin();
    }

    public String getMailFrom() {
        return delegate.getMailFrom();
    }

    public String getMailSubjectPrefix() {
        return delegate.getMailSubjectPrefix();
    }

    public String getClusterName() {
        return delegate.getClusterName();
    }

    public String getNodeBmaHost() {
        return delegate.getNodeBmaHost();
    }

    public int getNodeBmaPort() {
        return delegate.getNodeBmaPort();
    }

    public int getIndexBulkSize() {
        return delegate.getIndexBulkSize();
    }

    public boolean enableBlockchainSync() {
        return delegate.enableBlockchainSync();
    }

    public String getKeyringSalt() {
        return delegate.getKeyringSalt();
    }

    public String getKeyringPassword() {
        return delegate.getKeyringPassword();
    }

    public String getKeyringPublicKey() {
        return delegate.getKeyringPublicKey();
    }

    public String getKeyringSecretKey() {
        return delegate.getKeyringSecretKey();
    }

    public String getDefaultStringAnalyzer() {
        return delegate.getDefaultStringAnalyzer();
    }

    /* -- protected methods -- */

    protected String getI18nBundleName() {
        return "gchange-pod-es-plugin-i18n";
    }
}
