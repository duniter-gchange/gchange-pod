package org.duniter.elasticsearch.gchange.model.market;

import org.duniter.core.client.model.elasticsearch.Record;

import java.util.Map;

public class MarketCategoryRecord extends Record {

    public static final String PROPERTY_NAME="name";
    public static final String PROPERTY_PARENT="parent";
    public static final String PROPERTY_LOCALIZED_NAMES="localizedNames";
    public static final String PROPERTY_ORDER="order";

    private String name;
    private String parent;
    private int order;

    private Map<String, String> localizedNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, String> getLocalizedNames() {
        return localizedNames;
    }

    public void setLocalizedNames(Map<String, String> localizedNames) {
        this.localizedNames = localizedNames;
    }
}
