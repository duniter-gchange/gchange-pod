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


import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.duniter.core.client.config.Configuration;
import org.duniter.core.client.config.ConfigurationOption;
import org.duniter.core.client.config.ConfigurationProvider;
import org.duniter.core.client.model.local.Peer;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.util.StringUtils;
import org.elasticsearch.common.component.*;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.nuiton.config.ApplicationConfig;
import org.nuiton.config.ApplicationConfigHelper;
import org.nuiton.config.ApplicationConfigProvider;
import org.nuiton.config.ArgumentsParserException;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import static org.nuiton.i18n.I18n.t;

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

    /* -- delegate methods -- */

    public boolean reloadIndices() {
        return delegate.reloadIndices();
    }

    public boolean enableDataSync() {
        return delegate.enableDataSync();
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
