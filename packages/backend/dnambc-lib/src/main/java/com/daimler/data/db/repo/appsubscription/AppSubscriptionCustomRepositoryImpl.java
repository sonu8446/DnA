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

package com.daimler.data.db.repo.appsubscription;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.daimler.data.db.entities.AppSubscriptionNsql;
import com.daimler.data.db.repo.common.CommonDataRepositoryImpl;

@Repository
public class AppSubscriptionCustomRepositoryImpl
        extends CommonDataRepositoryImpl<AppSubscriptionNsql, String>
        implements AppSubscriptionCustomRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(AppSubscriptionCustomRepositoryImpl.class);
	
	/**
	 * get All Subscribed record based on filer parameter
	 * 
	 * @param userId
	 * @param isAdmin
	 * @param recordStatus
	 * @param offset
	 * @param limit
	 * @return List<AppSubscriptionNsql>
	 */
	@Override
	public List<AppSubscriptionNsql> getAllWithFilters(String userId, boolean isAdmin, String recordStatus,
			String appId, String sortBy, String sortOrder, int offset, int limit, String appName) {
		LOGGER.trace("Entering getAllWithFilters");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppSubscriptionNsql> cq = cb.createQuery(AppSubscriptionNsql.class);
		Root<AppSubscriptionNsql> root = cq.from(AppSubscriptionNsql.class);
		CriteriaQuery<AppSubscriptionNsql> getAll = cq.select(root);

		Predicate consolidatedPredicate = buildPredicate(cb, root, userId, isAdmin, recordStatus, appId, appName);
		cq.where(consolidatedPredicate);
		Object[] literalExpressionAndProperties = this.getLiteralForVariable(cb, root, sortBy);
		Expression<?>[] sortByExpressions = (Expression<?>[]) literalExpressionAndProperties[0];
		boolean isNumber = (boolean) literalExpressionAndProperties[1];
		if (sortBy != null) {
			Expression<?> sortFunctionExpression = isNumber
					? cb.function("jsonb_extract_path", BigDecimal.class, sortByExpressions)
					: cb.lower(cb.function("jsonb_extract_path_text", String.class, sortByExpressions));
			if (sortOrder.equals("asc")) {
				cq.orderBy(cb.asc(sortFunctionExpression));
			} else {
				cq.orderBy(cb.desc(sortFunctionExpression));
			}
		} else {
			// default sort asc on appName
			cq.orderBy(cb.asc(cb.lower(
					cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("appName")))));
		}

		TypedQuery<AppSubscriptionNsql> getAllQuery = em.createQuery(getAll);
		if (offset >= 0)
			getAllQuery.setFirstResult(offset);
		if (limit > 0)
			getAllQuery.setMaxResults(limit);
		LOGGER.trace("Exiting getAllWithFilters");
		return getAllQuery.getResultList();
	}

	/**
	 * get total count of subscribed record based on user
	 * 
	 * @param userId
	 * @param isAdmin
	 * @return count
	 */
	@Override
	public Long getCount(String userId,boolean isAdmin, String recordStatus, String appId) {
		LOGGER.trace("Entering getCount");
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
	    Root<AppSubscriptionNsql> root = cq.from(AppSubscriptionNsql.class);
	    CriteriaQuery<Long> getAll = cq.select(cb.count(root));
	    Predicate consolidatedPredicate = buildPredicate(cb,root,userId,isAdmin,recordStatus, appId, null); 
	    cq.where(consolidatedPredicate);
	    TypedQuery<Long> getAllQuery = em.createQuery(getAll);
	    LOGGER.trace("Exiting getCount");
	    return getAllQuery.getSingleResult();
	}
	
	/**
	 * Build predicate with given parameters
	 * 
	 * @param cb
	 * @param root
	 * @param userId
	 * @param isAdmin
	 * @param recordStatus
	 * @return Predicate
	 */
	private Predicate buildPredicate(CriteriaBuilder cb, Root<AppSubscriptionNsql> root, String userId, boolean isAdmin,
			String recordStatus, String appId, String appName) {
		LOGGER.trace("Entering buildPredicate");
		Predicate pMain = cb.isNotNull(root.get("id"));
		if (StringUtils.hasText(userId) && !isAdmin) {
			LOGGER.debug("Adding userId in query predicate");
			Predicate inUserIdPredicate = cb.equal(
					cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("createdBy")),
					userId);

			pMain = cb.and(pMain, inUserIdPredicate);
		}
		if (StringUtils.hasText(recordStatus)) {
			LOGGER.debug("Adding recordStatus in query predicate");
			Predicate inRecordStatusPredicate = cb.equal(
					cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("recordStatus")),
					recordStatus);

			pMain = cb.and(pMain, inRecordStatusPredicate);
		}
		if (StringUtils.hasText(appId)) {
			LOGGER.debug("Adding appId in query predicate");
			Predicate inAppIdPredicate = cb.equal(
					cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("appId")),
					appId);

			pMain = cb.and(pMain, inAppIdPredicate);
		}
		if (StringUtils.hasText(appName)) {
			LOGGER.debug("Adding appId in query predicate");
			Predicate inAppNamePredicate = cb.equal(
					cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("appName")),
					appName);

			pMain = cb.and(pMain, inAppNamePredicate);
		}
		LOGGER.trace("Exiting buildPredicate");
		return pMain;
	}
	
	private Object[] getLiteralForVariable(CriteriaBuilder cb, Root<AppSubscriptionNsql> root, String inputVariable) {
		List<Expression<?>> expressions = new ArrayList<>();
		boolean isNumber = false;
		if(inputVariable != null) {
			switch(inputVariable) {
				case  "appName" : expressions.add(root.get("data")); expressions.add(cb.literal("appName")); break;
				case  "createdBy" : expressions.add(root.get("data")); expressions.add(cb.literal("createdBy")); break;
				case  "createdDate" : expressions.add(root.get("data")); expressions.add(cb.literal("createdDate")); isNumber = true; break;
				case  "updatedBy" : expressions.add(root.get("data")); expressions.add(cb.literal("updatedBy")); break;
				case  "updatedDate" : expressions.add(root.get("data")); expressions.add(cb.literal("updatedDate")); isNumber = true; break;
				case  "expireOn" : expressions.add(root.get("data")); expressions.add(cb.literal("expireOn")); isNumber = true; break;
				case  "lastUsedOn" : expressions.add(root.get("data")); expressions.add(cb.literal("lastUsedOn")); isNumber = true; break;
				case  "usageCount" : expressions.add(root.get("data")); expressions.add(cb.literal("usageCount")); isNumber = true; break;
				default : break;
			}
		}
		return new Object[]{expressions.toArray(new Expression[expressions.size()]),isNumber};
	}
	
}
