package org.duniter.elasticsearch.gchange.model.market;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.duniter.elasticsearch.model.Record;
import org.elasticsearch.common.geo.GeoPoint;

@Data
@FieldNameConstants
public class MarketRecordFilter extends Record {

    public static class Fields extends Record.Fields {}

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

}
