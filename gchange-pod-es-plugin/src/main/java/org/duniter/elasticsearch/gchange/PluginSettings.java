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


import com.google.common.base.Preconditions;
import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.elasticsearch.gchange.model.bma.GchangeEndpoindApi;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;

import java.util.Collection;
import java.util.Set;

/**
 * Access to configuration options
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class PluginSettings extends AbstractLifecycleComponent<PluginSettings> {

    private org.duniter.elasticsearch.user.PluginSettings delegate;

    @Inject
    public PluginSettings(org.elasticsearch.common.settings.Settings settings,
                          org.duniter.elasticsearch.PluginSettings corePluginSettings,
                          org.duniter.elasticsearch.user.PluginSettings userPluginSettings) {
        super(settings);
        this.delegate = userPluginSettings;

        // Add i18n bundle name
        userPluginSettings.addI18nBundleName(getI18nBundleName());

        // Override the version of the pod software
        corePluginSettings.setSoftwareDefaultVersion(getPackageVersion());
    }

    @Override
    protected void doStart() {

        // Check config options
        checkEndpointApis();
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

    public boolean synchronizeClosedAds() {
        return this.settings.getAsBoolean("duniter.p2p.market.closedAd", Boolean.FALSE);
    }

    /* -- delegate methods -- */

    public int getPeeringInterval() {
        return delegate.getDelegate().getPeeringInterval();
    }

    public int getScanPeerInterval() {
        return this.settings.getAsInt("duniter.p2p.scan.interval", getPeeringInterval());
    }

    public Set<String> getDocumentAdminAndModeratorsPubkeys() {
        return this.delegate.getDocumentAdminAndModeratorsPubkeys();
    }

    public String getShareSiteName() {
        return delegate.getShareSiteName();
    }

    public String getShareDefaultImageUrl() {
        return delegate.getShareDefaultImageUrl();
    }

    public String getClusterRemoteUrlOrNull() {
        return delegate.getClusterRemoteUrlOrNull();
    }

    public boolean reloadAllIndices() {
        return delegate.reloadAllIndices();
    }


    public Collection<String> getPeeringTargetedApis() {
        return this.delegate.getPeeringTargetedApis();
    }

    public Collection<String> getPeeringPublishedApis() {
        return this.delegate.getPeeringPublishedApis();
    }

    public boolean enableDocStats() {
        return delegate.enableDocStats();
    }

    public String getDefaultStringAnalyzer() {
        return delegate.getDefaultStringAnalyzer();
    }

    /* -- protected methods -- */

    protected String getI18nBundleName() {
        return "gchange-pod-es-plugin-i18n";
    }


    /**
     * Override the version default option, from the MANIFEST implementation version (if any)
     * @param applicationConfig
     */
    protected String getPackageVersion() {
        // Override application version
        Package currentPackage = this.getClass().getPackage();
        String newVersion = currentPackage.getImplementationVersion();
        if (newVersion == null) {
            newVersion = currentPackage.getSpecificationVersion();
        }
        return newVersion;
    }


    protected void checkEndpointApis() {
        Preconditions.checkArgument(GchangeEndpoindApi.GCHANGE_API.label().equals(EndpointApi.ES_CORE_API.label()),
                String.format("Please set config option 'duniter.core.api=%s'", GchangeEndpoindApi.GCHANGE_API.label()));

        Preconditions.checkArgument(GchangeEndpoindApi.GCHANGE_API.label().equals(EndpointApi.ES_USER_API.label()),
                String.format("Please set config option 'duniter.user.api=%s'", GchangeEndpoindApi.GCHANGE_API.label()));

        Preconditions.checkArgument(GchangeEndpoindApi.GCHANGE_SUBSCRIPTION_API.label().equals(EndpointApi.ES_SUBSCRIPTION_API.label()),
                String.format("Please set config option 'duniter.subscription.api=%s'", GchangeEndpoindApi.GCHANGE_SUBSCRIPTION_API.label()));
    }
}
