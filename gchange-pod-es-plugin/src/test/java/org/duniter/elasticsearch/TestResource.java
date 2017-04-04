package org.duniter.elasticsearch;

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


import com.google.common.collect.Lists;
import org.duniter.core.client.config.ConfigurationOption;
import org.duniter.core.client.service.ServiceLocator;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TestResource extends org.duniter.core.test.TestResource {

    private static final Logger log = LoggerFactory.getLogger(TestResource.class);

    public static TestResource create() {
        return new TestResource(null);
    }
    
    public static TestResource create(String configName) {
        return new TestResource(configName);
    }

    private TestFixtures fixtures = new TestFixtures();

    protected TestResource(String configName) {
        super(configName);
    }
    
    public TestFixtures getFixtures() {
        return fixtures;
    }

    protected void before(Description description) throws Throwable {
        super.before(description);

        // Initialize configuration
        initConfiguration(getConfigFileName());

        // Init i18n
        initI18n();

        // Initialize service locator
        ServiceLocator.instance().init();
    }

    /**
     * Return configuration files prefix (i.e. 'allegro-test')
     * Could be override by external project
     * 
     * @return the prefix to use to retrieve configuration files
     */
    protected String getConfigFilesPrefix() {
        return "duniter4j-elasticsearch-test";
    }
    
    protected String getI18nBundleName() {
        return "duniter4j-elasticsearch-i18n";
    }

    /* -- -- */

    /**
     * Convenience methods that could be override to initialize other configuration
     *
     * @param configFilename
     * @param configArgs
     */
    protected void initConfiguration(String configFilename) {
        String[] configArgs = getConfigArgs();
        //PluginSettings config = new PluginSettings(configFilename, configArgs);
        //PluginSettings.setInstance(config);
    }

    protected void initI18n() throws IOException {
        /*PluginSettings config ;//= PluginSettings.instance();

        // --------------------------------------------------------------------//
        // init i18n
        // --------------------------------------------------------------------//
        File i18nDirectory = new File(config.getDataDirectory(), "i18n");
        if (i18nDirectory.exists()) {
            // clean i18n cache
            FileUtils.cleanDirectory(i18nDirectory);
        }

        FileUtils.forceMkdir(i18nDirectory);

        if (log.isDebugEnabled()) {
            log.debug("I18N directory: " + i18nDirectory);
        }

        Locale i18nLocale = config.getI18nLocale();

        if (log.isInfoEnabled()) {
            log.info(String.format("Starts i18n with locale [%s] at [%s]",
                    i18nLocale, i18nDirectory));
        }
        I18n.init(new UserI18nInitializer(
                        i18nDirectory, new DefaultI18nInitializer(getI18nBundleName())),
                i18nLocale);*/
    }

    protected String[] getConfigArgs() {
        List<String> configArgs = Lists.newArrayList();
        configArgs.addAll(Lists.newArrayList(
                "--option", ConfigurationOption.BASEDIR.getKey(), getResourceDirectory().getAbsolutePath()));
        return configArgs.toArray(new String[configArgs.size()]);
    }

}
