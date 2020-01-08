package org.duniter.elasticsearch.gchange.rest.like;

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
import org.duniter.elasticsearch.gchange.dao.market.MarketCommentDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketIndexDao;
import org.duniter.elasticsearch.gchange.dao.market.MarketRecordDao;
import org.duniter.elasticsearch.gchange.service.MarketService;
import org.duniter.elasticsearch.rest.security.RestSecurityController;
import org.duniter.elasticsearch.user.dao.CommentDao;
import org.duniter.elasticsearch.user.dao.group.GroupIndexDao;
import org.duniter.elasticsearch.user.dao.page.PageIndexDao;
import org.duniter.elasticsearch.user.dao.page.PageRecordDao;
import org.duniter.elasticsearch.user.rest.like.RestLikeGetAction;
import org.duniter.elasticsearch.user.rest.like.RestLikePostAction;
import org.duniter.elasticsearch.user.service.UserService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.rest.RestRequest;

/**
 * Created by blavenie on 13/12/16.
 */
public class RestLikeAction {

    @Inject
    public RestLikeAction(RestLikeGetAction getAction,
                          RestLikePostAction postAction) {

        getAction.allowLikeIndex(MarketIndexDao.INDEX, MarketRecordDao.TYPE);
        postAction.allowLikeIndex(MarketIndexDao.INDEX, MarketRecordDao.TYPE);

        getAction.allowLikeIndex(MarketIndexDao.INDEX, MarketCommentDao.TYPE);
        postAction.allowLikeIndex(MarketIndexDao.INDEX, MarketCommentDao.TYPE);
    }
}