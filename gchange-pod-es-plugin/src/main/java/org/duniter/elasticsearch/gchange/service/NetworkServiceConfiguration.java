package org.duniter.elasticsearch.gchange.service;

import org.duniter.core.beans.Bean;
import org.duniter.core.client.model.bma.EndpointApi;
import org.duniter.core.util.CollectionUtils;
import org.duniter.elasticsearch.service.NetworkService;
import org.duniter.elasticsearch.service.PeerService;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.elasticsearch.common.inject.Inject;

public class NetworkServiceConfiguration implements Bean {


    @Inject
    public NetworkServiceConfiguration(PeerService peerService,
                                       PluginSettings pluginSettings,
                                       NetworkService networkService) {

        // Retrieve peer with the GCHANGE_API api
        peerService.addIncludeEndpointApi(EndpointApi.GCHANGE_API);

        // Register ES_USER_API, if list of APIs has not already defined in settings
        if (CollectionUtils.isEmpty(pluginSettings.getPeeringPublishedApis())) {
            networkService.addPublishEndpointApi(EndpointApi.GCHANGE_API);
        }

    }
}
