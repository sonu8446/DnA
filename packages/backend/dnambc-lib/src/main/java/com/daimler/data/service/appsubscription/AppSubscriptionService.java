/* LICENSE START
 * 
 * MIT License
 * 
 * Copyright (c) 2019 Daimler TSS GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * LICENSE END 
 */

package com.daimler.data.service.appsubscription;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.daimler.data.db.entities.AppSubscriptionNsql;
import com.daimler.data.dto.appsubscription.ApiKeyValidationResponseVO;
import com.daimler.data.dto.appsubscription.ApiKeyValidationVO;
import com.daimler.data.dto.appsubscription.SubscriptionExpireVO;
import com.daimler.data.dto.appsubscription.SubscriptionVO;
import com.daimler.data.service.common.CommonService;

public interface AppSubscriptionService extends CommonService<SubscriptionVO, AppSubscriptionNsql, String> {

	/**
	 * <p>
	 * To validate application key.
	 * </p>
	 * 
	 * @param ApiKeyValidationVO
	 */
	public ResponseEntity<ApiKeyValidationResponseVO> validateApiKey(ApiKeyValidationVO apiKeyValidationVO);

	/**
	 * create/update api key in vault
	 * 
	 * @param SubscriptionVO
	 * @return SubscriptionVO
	 */
	public SubscriptionVO createApiKey(SubscriptionVO requestSubscriptionVO, String userId);

	/**
	 * get All Subscription details based on filter
	 * 
	 * @param userId
	 * @param isAdmin
	 * @param offset
	 * @param limit
	 * @return List<SubscriptionVO>
	 */
	public List<SubscriptionVO> getAllWithFilters(String userId, boolean isAdmin, String recordStatus, String appId,
			String sortBy, String sortOrder, int offset, int limit);

	/**
	 * <p>
	 * get count of subscribed application
	 * </p>
	 * 
	 * @param userId
	 * @param isAdmin
	 */
	public Long getCount(String userId, boolean isAdmin, String recordStatus, String appId);

	/**
	 * <p>
	 * Delete a subscription with given identifier
	 * </p>
	 * 
	 * @param id
	 * @param userId
	 * @param existingSubscriptionVO
	 */
	public SubscriptionVO deleteSubscriptionById(String id, String userId, SubscriptionVO existingSubscriptionVO);

	/**
	 * <p>
	 * Refresh apiKey based on given appId
	 * </p>
	 * 
	 * @param appId
	 * @return SubscriptionVO
	 */
	public SubscriptionVO refreshApiKeyByAppId(String appId, String userId, SubscriptionVO subscriptionVO);

	/**
	 * <p>
	 * Link provisioned solution id for Malware scan service subscription
	 * </p>
	 * 
	 * @param appId
	 * @param solutionId
	 */
	public void updateSolIdForSubscribedAppId(String appId, String solutionId);
	
	/**
	 * <p>
	 * Update subscription as set expiry days.
	 * </p>
	 * 
	 * @param userId
	 * @param expireVO
	 * @return SubscriptionVO
	 */
	public SubscriptionVO updateSubscription(String userId, SubscriptionExpireVO expireVO);
	
	/**
	 * <p>
	 * isApplicationSubscriptionExist checks whether the application lready exist or not
	 * </p>
	 * 
	 * @param userId
	 * @param expireVO
	 * @return SubscriptionVO
	 */
	public boolean isApplicationSubscriptionExist(SubscriptionVO requestSubscriptionVO, String userId);
}
