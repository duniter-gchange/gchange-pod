package org.duniter.elasticsearch.gchange.model.market;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.duniter.core.model.IEntity;

@Data
@FieldNameConstants
public class LightCategory implements IEntity<String> {

    private String id;
    private String name;
    private String parent;
    private int order;

    public LightCategory() {

    }
    public LightCategory(String id) {
        this.id = id;
    }

}
