package org.duniter.elasticsearch.gchange.model.bma;

import org.duniter.core.client.model.bma.IEndpointApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GchangeEndpoindApi implements IEndpointApi {

    GCHANGE_API(),

    GCHANGE_SUBSCRIPTION_API()
    ;


    private static final Logger log = LoggerFactory.getLogger(GchangeEndpoindApi.class);

    private String label;

    GchangeEndpoindApi(String label) {
        this.label = label;
    }

    GchangeEndpoindApi() {
        this.label = this.name();
    }

    public String label() {
        return this.label;
    }

    /**
     * Allow to change the API label.
     * Useful for reuse and API enumeration, with a new label (eg: ES_CORE_API => GCHANGE_API)
     * @param api
     * @param label
     */
    public void setLabel(String label) {
        if (!this.label.equals(label)) {
            log.warn(String.format("Endpoint API '%s' label change to '%s'", this.name(), label));
            this.label = label;
        }
    }
}
