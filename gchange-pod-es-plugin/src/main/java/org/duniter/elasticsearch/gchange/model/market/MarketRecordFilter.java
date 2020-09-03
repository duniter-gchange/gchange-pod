package org.duniter.elasticsearch.gchange.model.market;

import org.duniter.core.client.model.elasticsearch.Record;
import org.elasticsearch.common.geo.GeoPoint;

public class MarketRecordFilter extends Record {

    private String id;

    private String currency;

    private MarketRecord.Type type;

    private LightCategory category;

    private String searchText;

    private String location;

    private GeoPoint geoPoint;

    private GeoPoint[] geoPolygon;

    private String geoShapeId;

    private String geoDistance;

    private boolean withOld = false;

    private boolean withClosed = false;


    private String[] fieldNames;

    private int size;

    private int from;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public LightCategory getCategory() {
        return category;
    }

    public void setCategory(LightCategory category) {
        this.category = category;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public GeoPoint[] getGeoPolygon() {
        return geoPolygon;
    }

    public void setGeoPolygon(GeoPoint[] geoPolygon) {
        this.geoPolygon = geoPolygon;
    }

    public String getGeoShapeId() {
        return geoShapeId;
    }

    public void setGeoShapeId(String geoShapeId) {
        this.geoShapeId = geoShapeId;
    }

    public String getGeoDistance() {
        return geoDistance;
    }

    public void setGeoDistance(String geoDistance) {
        this.geoDistance = geoDistance;
    }

    public boolean isWithOld() {
        return withOld;
    }

    public void setWithOld(boolean withOld) {
        this.withOld = withOld;
    }

    public boolean isWithClosed() {
        return withClosed;
    }

    public void setWithClosed(boolean withClosed) {
        this.withClosed = withClosed;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public MarketRecord.Type getType() {
        return type;
    }

    public void setType(MarketRecord.Type type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }
}
