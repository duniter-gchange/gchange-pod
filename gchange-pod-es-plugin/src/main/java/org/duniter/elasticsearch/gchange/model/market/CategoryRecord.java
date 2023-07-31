package org.duniter.elasticsearch.gchange.model.market;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.duniter.elasticsearch.model.Record;

import java.util.Map;

@Data
@FieldNameConstants
public class CategoryRecord extends Record {

    public static class Fields extends Record.Fields {}

    private String name;
    private String parent;
    private int order;
    private Long creationTime;
    private Map<String, String> localizedNames;

}
