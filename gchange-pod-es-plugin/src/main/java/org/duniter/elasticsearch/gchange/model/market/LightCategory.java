package org.duniter.elasticsearch.gchange.model.market;

import org.duniter.core.client.model.local.LocalEntity;

public class LightCategory implements LocalEntity<String> {

    public static final String PROPERTY_NAME = CategoryRecord.PROPERTY_NAME;
    public static final String PROPERTY_PARENT = CategoryRecord.PROPERTY_PARENT;
    public static final String PROPERTY_ORDER = CategoryRecord.PROPERTY_ORDER;

    private String id;
    private String name;
    private String parent;
    private int order;

    public LightCategory() {

    }
    public LightCategory(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
