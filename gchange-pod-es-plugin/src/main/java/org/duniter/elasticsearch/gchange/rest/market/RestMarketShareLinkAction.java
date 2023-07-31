package org.duniter.elasticsearch.gchange.rest.market;

import com.google.common.collect.ImmutableMap;
import com.google.common.html.HtmlEscapers;
import org.duniter.core.exception.BusinessException;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.util.StringUtils;
import org.duniter.elasticsearch.exception.DuniterElasticsearchException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordRepository;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.model.Records;
import org.duniter.elasticsearch.rest.attachment.RestImageAttachmentAction;
import org.duniter.elasticsearch.rest.share.AbstractRestShareLinkAction;
import org.duniter.elasticsearch.service.CurrencyService;
import org.duniter.elasticsearch.util.opengraph.OGData;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.rest.RestController;
import org.nuiton.i18n.I18n;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class RestMarketShareLinkAction extends AbstractRestShareLinkAction implements AbstractRestShareLinkAction.OGDataResolver {

    private static final Map<String, String> WELL_KNOWN_CURRENCY_SYMBOLS = ImmutableMap.<String, String>builder()
            .put("g1", "Ğ1")
            .put("g1-test", "Ğ1-test")
            .build();

    private final PluginSettings pluginSettings;
    private final CurrencyService currencyService;
    private final MarketService marketService;

    @Inject
    public RestMarketShareLinkAction(final PluginSettings pluginSettings, final RestController controller, final Client client,
                                     final MarketService marketService,
                                     final CurrencyService currencyService) {
        super(pluginSettings.getDelegate().getDelegate(), controller, client, MarketIndexRepository.INDEX, MarketRecordRepository.TYPE);
        setResolver(this);
        this.pluginSettings = pluginSettings;
        this.marketService = marketService;
        this.currencyService = currencyService;
    }

    @Override
    public OGData resolve(String id) throws DuniterElasticsearchException, BusinessException {
        try {
            MarketRecord record = marketService.getRecordForSharing(id);

            OGData data = new OGData();
            String siteName = pluginSettings.getShareSiteName();

            if (record != null) {

                // og:title
                if (StringUtils.isNotBlank(record.getTitle())) {
                    data.title = record.getTitle();

                    // Add price to title
                    String formattedPrice = getFormattedPrice(record);
                    if (formattedPrice != null) {
                        // Append to title
                        data.title += " | " + formattedPrice;
                    }
                }
                else {
                    data.title = siteName;
                }

                // og:description
                data.description = HtmlEscapers.htmlEscaper().escape(record.getDescription());

                // og:image
                if (record.getThumbnail() != null && StringUtils.isNotBlank(record.getThumbnail().get("_content_type"))) {
                    String baseUrl = pluginSettings.getClusterRemoteUrlOrNull();
                    data.image = StringUtils.isBlank(baseUrl) ? "" : baseUrl;
                    data.image += RestImageAttachmentAction.computeImageUrl(MarketIndexRepository.INDEX, MarketRecordRepository.TYPE, id, MarketRecord.Fields.THUMBNAIL, record.getThumbnail().get("_content_type"));

                    // At least 200x200px (need by social network as FB)
                    data.imageHeight = OGData.MIN_IMAGE_HEIGHT;
                    data.imageWidth = OGData.MIN_IMAGE_WIDTH;
                }

                // og:url
                data.url = pluginSettings.getShareMarketLinkUrl()
                        .replace("{id}", id)
                        .replace("{title}", URLEncoder.encode(record.getTitle(), "UTF-8"));
            }
            else {

                // og:title
                data.title = siteName;

                // og:description
                data.description = I18n.t("duniter.market.share.description");

                // og:url
                data.url = pluginSettings.getShareMarketLinkUrl()
                        .replace("{id}", id)
                        .replace("{title}", "");
            }

            // og:type
            data.type = "website";

            // og:site_name
            data.siteName = siteName;

            // default og:image
            if (StringUtils.isBlank(data.image)) {
                data.image = pluginSettings.getShareDefaultImageUrl();
                data.imageType = "image/png";
                data.imageHeight = OGData.MIN_IMAGE_HEIGHT;
                data.imageWidth = OGData.MIN_IMAGE_WIDTH;
            }

            return data;
        }
        catch(UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the formatted price
     * @param record
     */
    protected String getFormattedPrice(MarketRecord record) {

        if (record.getPrice() == null || StringUtils.isBlank(record.getCurrency())) return null;

        // Using parse allow to use string in the field
        double price = record.getPrice();

        // Convert UD price into absolute price
        if ("UD".equalsIgnoreCase(record.getUnit())) {
            try {
                Long lastUD = currencyService.getDividend(record.getCurrency());
                if (lastUD == null) return null; // Unable to convert (no last UD)

                price = price * (currencyService.getDividend(record.getCurrency()) / 100);
                price = (Math.round(price * 100) / 100); // round to 2 decimal
            } catch(Exception e) {
                logger.error(String.format("Cannot convert price of record %s: %s", record.getId()), e.getMessage());
            }
        }
        else {
            price = price / 100;
        }

        StringBuilder result = new StringBuilder();
        result.append(price);

        // Remove unused decimal
        if (result.toString().endsWith(".0")) {
            result.setLength(result.length() - 2);
        }

        return result
                .append(' ')
                .append(getCurrencySymbol(record.getCurrency()))
                .toString();
    }

    protected String getCurrencySymbol(String currency) {
        String symbol = WELL_KNOWN_CURRENCY_SYMBOLS.get(currency);
        return symbol != null ? symbol : currency;
    }
}
