package org.duniter.elasticsearch.gchange.rest.market;

import com.google.common.html.HtmlEscapers;
import org.duniter.core.exception.BusinessException;
import org.duniter.core.exception.TechnicalException;
import org.duniter.core.util.StringUtils;
import org.duniter.elasticsearch.exception.DuniterElasticsearchException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.model.market.MarketRecord;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.rest.attachment.RestImageAttachmentAction;
import org.duniter.elasticsearch.rest.share.AbstractRestShareLinkAction;
import org.duniter.elasticsearch.user.dao.page.PageIndexDao;
import org.duniter.elasticsearch.user.dao.page.PageRecordDao;
import org.duniter.elasticsearch.user.model.page.RegistryRecord;
import org.duniter.elasticsearch.util.opengraph.OGData;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.rest.RestController;
import org.nuiton.i18n.I18n;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RestMarketShareLinkAction extends AbstractRestShareLinkAction implements AbstractRestShareLinkAction.OGDataResolver {

    private final PluginSettings pluginSettings;
    private final MarketService service;

    @Inject
    public RestMarketShareLinkAction(final PluginSettings pluginSettings, final RestController controller, final Client client,
                                     final MarketService service) {
        super(pluginSettings.getDelegate().getDelegate(), controller, client, MarketIndexDao.INDEX, MarketRecordDao.TYPE);
        setResolver(this);
        this.pluginSettings = pluginSettings;
        this.service = service;
    }

    @Override
    public OGData resolve(String id) throws DuniterElasticsearchException, BusinessException {
        try {
            MarketRecord record = service.getRecordForSharing(id);

            OGData data = new OGData();
            String siteName = pluginSettings.getDelegate().getShareSiteName();

            if (record != null) {

                // og:title
                if (StringUtils.isNotBlank(record.getTitle())) {
                    data.title = record.getTitle();
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
                    data.image += RestImageAttachmentAction.computeImageUrl(MarketIndexDao.INDEX, MarketRecordDao.TYPE, id, MarketRecord.PROPERTY_THUMBNAIL, record.getThumbnail().get("_content_type"));

                    // At least 200x200px (need by social network as FB)
                    data.imageHeight = 200;
                    data.imageWidth = 200;
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
                data.image = pluginSettings.getDelegate().getShareDefaultImageUrl();
                data.imageType = "image/png";
                data.imageHeight = 200;
                data.imageWidth = 200;
            }

            return data;
        }
        catch(UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }
    }

}
