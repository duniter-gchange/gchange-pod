package org.duniter.elasticsearch.gchange.model.auction;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.duniter.elasticsearch.model.Record;

@Data
@FieldNameConstants
public class AuctionRecord extends Record {

    public static class Fields extends Record.Fields {}

    private Double price;
    private String unit;

    private String currency;

    private Long creationTime;
}
