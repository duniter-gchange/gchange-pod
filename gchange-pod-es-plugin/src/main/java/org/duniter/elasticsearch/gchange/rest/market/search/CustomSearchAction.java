package org.duniter.elasticsearch.gchange.rest.market.search;

import org.apache.commons.lang3.StringUtils;
import org.duniter.core.exception.BusinessException;
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.exception.DuniterElasticsearchException;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.model.market.LightCategory;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.model.market.MarketRecordFilter;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.rest.RestXContentBuilder;
import org.duniter.elasticsearch.rest.XContentRestResponse;
import org.duniter.elasticsearch.rest.XContentThrowableRestResponse;
import org.duniter.elasticsearch.rest.security.RestSecurityController;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class CustomSearchAction extends BaseRestHandler {

    private final ESLogger log;
    private MarketService service;

    @Inject
    public CustomSearchAction(final Settings settings,
                              final RestController controller,
                              final RestSecurityController securityController,
                              final Client client,
                              MarketService service) {
        super(settings, controller, client);
        this.service = service;
        log = Loggers.getLogger("gchange." + MarketIndexRepository.INDEX, settings, String.format("[%s]", MarketIndexRepository.INDEX));

        // Register GET request on /market?
        controller.registerHandler(GET, String.format("/%s/_api", MarketIndexRepository.INDEX),
                this);
        securityController.allow(GET, String.format("/%s/_api", MarketIndexRepository.INDEX));
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel restChannel, Client client) throws Exception {

        try {
            // Create the filter, from the request
            MarketRecordFilter filter =  createFilterFromParams(request);

            List<BytesReference> results = service.findByFilter(filter, BytesReference.class);

            restChannel.sendResponse(new XContentRestResponse(request, OK, toXContent(request, results)));
        }
        catch(DuniterElasticsearchException | BusinessException e) {
            log.error(e.getMessage(), e);
            restChannel.sendResponse(new XContentThrowableRestResponse(request, e));
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /* -- protected methods -- */
    protected MarketRecordFilter createFilterFromParams(RestRequest request) {
        MarketRecordFilter filter = new MarketRecordFilter();

        // Type
        String type = StringUtils.trimToNull(request.param("type"));
        if (type != null) {
            try {
                MarketRecord.Type typeEnum = MarketRecord.Type.valueOf(type.toLowerCase());
                filter.setType(typeEnum);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(String.format("Invalid 'type'. Expected values: %s", MarketRecord.Type.names().toString()));
            }
        }

        // Category
        String categoryId = StringUtils.trimToNull(request.param("category"));
        if (categoryId != null) {
            filter.setCategory(new LightCategory(categoryId));
        }

        // Search text
        filter.setSearchText(StringUtils.trimToNull(request.param("q")));

        // Location
        filter.setLocation(StringUtils.trimToNull(request.param("location")));

        // Geo Point
        float lat = request.paramAsFloat("lat", -999f);
        float lon = request.paramAsFloat("lon", -999);
        if (lat != -999 && lon != -999) {
            filter.setGeoPoint(new GeoPoint(lat, lon));
        }

        // Geo Shape
        filter.setGeoShapeId(StringUtils.trimToNull(request.param("shape")));

        // Geo distance
        filter.setGeoDistance(StringUtils.trimToNull(request.param("distance")));

        // With Old ?
        filter.setWithOld(request.paramAsBoolean("old", false));

        // With Closed ?
        filter.setWithClosed(request.paramAsBoolean("closed", false));

        // Currency
        filter.setCurrency(StringUtils.trimToNull(request.param("currency")));

        // Field names
        String fieldNames = StringUtils.trimToNull(request.param("_source"));
        if (fieldNames != null) {
            filter.setFieldNames(fieldNames.split(","));
        }

        // Page (size, from)
        filter.setSize(request.paramAsInt("size", 20));
        filter.setFrom(request.paramAsInt("from", 0));

        return filter;
    }

    public XContentBuilder toXContent(RestRequest request, List<BytesReference> results) {

        try {
            final XContentBuilder builder = RestXContentBuilder.restContentBuilder(request).startArray();

            for (BytesReference record: results) {
                builder.rawValue(record);
            }

            builder.endArray();

            return builder;
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while generating JSON for [/node/summary]: %s", ioe.getMessage()), ioe);
        }
    }

}
