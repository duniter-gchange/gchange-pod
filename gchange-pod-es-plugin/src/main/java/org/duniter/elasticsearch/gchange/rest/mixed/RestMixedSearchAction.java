package org.duniter.elasticsearch.gchange.rest.mixed;

/*
 * #%L
 * duniter4j-elasticsearch-plugin
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import com.google.common.base.Joiner;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexRepository;
import org.duniter.elasticsearch.rest.security.RestSecurityController;
import org.duniter.elasticsearch.user.dao.CommentRepository;
import org.duniter.elasticsearch.user.dao.group.GroupIndexRepository;
import org.duniter.elasticsearch.user.dao.page.PageIndexRepository;
import org.duniter.elasticsearch.user.dao.page.PageRecordRepository;
import org.duniter.elasticsearch.user.service.UserService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.rest.RestRequest;

/**
 * Created by blavenie on 13/12/16.
 */
public class RestMixedSearchAction {

    @Inject
    public RestMixedSearchAction(RestSecurityController securityController) {

        String[] paths = {
            // Allow search on profile + page + group + market
            String.format("/%s/%s/_search",
                    Joiner.on(',').join(UserService.INDEX, PageIndexRepository.INDEX, GroupIndexRepository.INDEX, MarketIndexRepository.INDEX),
                    Joiner.on(',').join(UserService.PROFILE_TYPE, PageRecordRepository.TYPE, CommentRepository.TYPE)),
        };

        for(String path: paths) {
            securityController.allow(RestRequest.Method.GET, path);
            securityController.allow(RestRequest.Method.POST, path);
        }
    }
}