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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResource extends org.duniter.elasticsearch.test.TestResource<TestFixtures>{

    private static final Logger log = LoggerFactory.getLogger(TestResource.class);

    public static TestResource create() {
        return new TestResource(null, true);
    }

    public static TestResource create(String configName) {
        return new TestResource(configName, true);
    }

    public static TestResource stopped() {
        return new TestResource(null, false);
    }

    protected TestResource(String configName, boolean startNode) {
        super(configName, "gchange-pod-es-plugin", startNode, new TestFixtures());
    }
}
