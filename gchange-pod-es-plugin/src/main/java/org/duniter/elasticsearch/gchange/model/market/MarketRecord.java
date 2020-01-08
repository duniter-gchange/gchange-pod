package org.duniter.elasticsearch.gchange.model.market;

/*
 * #%L
 * Duniter4j :: ElasticSearch GChange plugin
 * %%
 * Copyright (C) 2014 - 2017 EIS
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

import org.duniter.core.client.model.elasticsearch.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blavenie on 01/12/16.
 */
public class MarketRecord extends Record {

    public static final String PROPERTY_TITLE="title";
    public static final String PROPERTY_DESCRIPTION="description";
    public static final String PROPERTY_PRICE="price";
    public static final String PROPERTY_UNIT="unit";
    public static final String PROPERTY_CURRENCY="currency";
    public static final String PROPERTY_FEES="fees";
    public static final String PROPERTY_FEES_CURRENCY="feesCurrency";
    public static final String PROPERTY_THUMBNAIL="thumbnail";
    public static final String PROPERTY_STOCK="stock";
    public static final String PROPERTY_TYPE="type";

    private String title;
    private String description;
    private Map<String, String> thumbnail = new HashMap<>();
    private Double price;
    private String unit;
    private Double fees;
    private String feesCurrency;
    private String currency;
    private Integer stock;
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Map<String, String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public String getFeesCurrency() {
        return feesCurrency;
    }

    public void setFeesCurrency(String feesCurrency) {
        this.feesCurrency = feesCurrency;
    }
}
