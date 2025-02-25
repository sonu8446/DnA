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

package com.daimler.data.db.repo.solution;

import com.daimler.data.db.entities.SolutionNsql;
import com.daimler.data.db.jsonb.DataSource;
import com.daimler.data.db.jsonb.solution.Solution;
import com.daimler.data.db.repo.common.CommonDataRepositoryImpl;
import com.daimler.data.dto.dashboard.DatasourceWidgetVO;
import com.daimler.data.dto.dashboard.LocationWidgetVO;
import com.daimler.data.dto.dashboard.MilestoneWidgetVO;
import com.daimler.data.dto.solution.DataVolumeVO;
import com.daimler.data.dto.solution.SolutionLocationVO;
import com.daimler.data.dto.solution.SolutionPhaseVO;
import com.daimler.data.util.ConstantsUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SolutionCustomRepositoryImpl
			extends CommonDataRepositoryImpl<SolutionNsql, String>
        		implements SolutionCustomRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(SolutionCustomRepositoryImpl.class);
	private static String noBookMarkId = "NOBOOKMARK";
	
	
	public Object executeCountQuery(String conditionalpredicate) {
		String prefix = "select count(*) from solution_nsql";
		String basicpredicate = " where (id is not null)";
		String query = prefix +  basicpredicate + conditionalpredicate;
		Query q = em.createNativeQuery(query);
		Object result = q.getResultList();
		System.out.println(result);
		return null;
	}
	
	private String getPublishPredicateString(Boolean published, String userId, Boolean isAdmin) {
		String allTrueCondition = " (jsonb_extract_path_text(data,'publish') in ('true')) ";
		String allFalseCondition = " (jsonb_extract_path_text(data,'publish') in ('false')) ";
		isAdmin = false;
		String isCreator = null;
		String userCreatedDraftsOnly = null;
		String publishQuery = "";
		if (userId != null) {
			isCreator = " lower(jsonb_extract_path_text(data,'createdBy','id')) like " + "'%" + userId.toLowerCase()
					+ "%'";
			userCreatedDraftsOnly = " ( " + allFalseCondition + " and " + isCreator + " ) ";
		}
		if (published != null) {
			if (published || isAdmin) {
				String requestedPublishState = published ? allTrueCondition : allFalseCondition;
				publishQuery = publishQuery + " and " + requestedPublishState;
			}
			if (!published && !isAdmin && userCreatedDraftsOnly != null) {
				publishQuery = publishQuery + " and " + userCreatedDraftsOnly;
			}
		} else {
			if (!isAdmin && userCreatedDraftsOnly != null) {
				String requestedPublishState = " and ( " + allTrueCondition + " or " + userCreatedDraftsOnly + " ) ";
				publishQuery = publishQuery + " and " + requestedPublishState;
			}
		}
		return publishQuery;
	}
	
	private String getSolutionTypePredicateString(String solutionType, String userId, List<String> bookmarkedSolutions) {
		if(solutionType!=null && !"".equalsIgnoreCase(solutionType)) {
			if("1".equalsIgnoreCase(solutionType)) {
				if(bookmarkedSolutions == null || bookmarkedSolutions.isEmpty()) {
					bookmarkedSolutions.add(noBookMarkId);
				}
				String commaSeparatedBookmarkedIds = bookmarkedSolutions.stream().collect(Collectors.joining("','","'","'"));
				return " and (id in (" + commaSeparatedBookmarkedIds +")))";
			}
			if("2".equalsIgnoreCase(solutionType)){
				String  isCreator = " lower(jsonb_extract_path_text(data,'createdBy','id')) like " + "'%" + userId.toLowerCase() + "%'"; 
				String isMemberPredicate =  " lower(jsonb_extract_path_text(data,'teamMembers')) like " + "'%" + userId.toLowerCase() + "%'"; 
				String currentUserPredicate = " and ( " + isMemberPredicate + " or " + isCreator + " ) ";
				return currentUserPredicate;
			}
		}
		return "";
	}
	
	private String getPhasesPredicateString(List<String> phaseIds) {
		if(phaseIds !=null && !phaseIds.isEmpty()) {
			String commaSeparatedPhaseIds = phaseIds.stream().collect(Collectors.joining("','","'","'"));
			return "  and ((jsonb_extract_path_text(data,'currentPhase','id') in (" + commaSeparatedPhaseIds + ")))";
		}
		return "";
	}
	
	private String getDataVolumesPredicateString(List<String> dataVolumes) {
		if(dataVolumes !=null && !dataVolumes.isEmpty()) {
			String commaSeparateddataVolumes = dataVolumes.stream().collect(Collectors.joining("','","'","'"));
			return "  and ((jsonb_extract_path_text(data,'totalDataVolume','id') in (" + commaSeparateddataVolumes + ")))";
		}
		return "";
	}
	
	private String getProjectStatusesPredicateString(List<String> statuses) {
		if(statuses !=null && !statuses.isEmpty()) {
			String commaSeparatedstatuses = statuses.stream().collect(Collectors.joining("','","'","'"));
			return "  and ((jsonb_extract_path_text(data,'projectStatus','id') in (" + commaSeparatedstatuses + "))) ";
		}
		return "";
	}
	
	private String getLocationsPredicateString(List<String> locations) {
		if(locations != null && !locations.isEmpty()) {
			String commaSeparatedLocations = locations.stream().map(s -> "%\"" + s + "\"%")
					  .collect(Collectors.joining("|"));
			String locationPredicate =  "and (jsonb_extract_path_text(data,'locations') similar to '" + commaSeparatedLocations + "' )";
			return locationPredicate;
		}
		return "";
	}
	
	private String getSearchTermsPredicateString(List<String> searchTerms) {
		if(searchTerms != null && !searchTerms.isEmpty()) {
			String delimiterSeparatedSearchTerms = searchTerms.stream().collect(Collectors.joining("|","%","%"));
			delimiterSeparatedSearchTerms = "'"+delimiterSeparatedSearchTerms+"'";
			return "  and ("
			+ "lower(jsonb_extract_path_text(data,'productName')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'tags')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'dataSources')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'platforms')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'languages')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'algorithms')) similar to " + delimiterSeparatedSearchTerms + " or "
			+ "lower(jsonb_extract_path_text(data,'visualizations')) similar to " + delimiterSeparatedSearchTerms 
			+ " ) ";
		}
		return "";
	}
	
	private String getTagsPredicateString(List<String> tags) {
		if(tags != null && !tags.isEmpty()) {
			String delimiterSeparatedTags = tags.stream().collect(Collectors.joining("|","%","%"));
			delimiterSeparatedTags = "'"+delimiterSeparatedTags+"'";
			return "  and (lower(jsonb_extract_path_text(data,'tags')) similar to " + delimiterSeparatedTags + " ) " ;
		}
		return "";
	}
	
	private String getRelatedProductsPredicateString(List<String> relatedProducts) {
		if(relatedProducts != null && !relatedProducts.isEmpty()) {
			String delimiterSeparatedRelatedProducts = relatedProducts.stream().collect(Collectors.joining("'|'","%","%"));
			return "  and (lower(jsonb_extract_path_text(data,'relatedProducts')) similar to " + delimiterSeparatedRelatedProducts + " ) ";
		}
		return "";
	}
	
	private String getDivisionsPredicateString(List<Map<String, List<String>>> divisionsList) {
		if (null != divisionsList && !divisionsList.isEmpty()) {
			List<String> onlyDivisions = new ArrayList<String>();
			String inDivSubdivConsolidated = null;
			for (Map<String, List<String>> divSubdivMap : divisionsList) {
				for (String div : divSubdivMap.keySet()) {
					String inDivSubdivs = null;
					if (null == divSubdivMap.get(div) || divSubdivMap.get(div).isEmpty()) {
						onlyDivisions.add(div);
					} else {
						// Adding Division in predicate
						String commaSeparatedinDivisions = Arrays.asList(div).stream()
								.collect(Collectors.joining("','", "'", "'"));
						String inDivisions = " (jsonb_extract_path_text(data,'division','id') in ("
								+ commaSeparatedinDivisions + ")) ";

						List<String> subdivisionList = divSubdivMap.get(div);
						String inNullSubdivisions = null;
						if (subdivisionList.contains(ConstantsUtility.EMPTY_VALUE)) {
							// adding null subdivision in predicate
							inNullSubdivisions = " (jsonb_extract_path_text(data,'division','subdivision','id') is null) ";
						}

						String inSubdivisions = null;
						if (!subdivisionList.isEmpty()) {
							// Adding subdivisions in predicate
							String commaSeparatedSubdivisionList = subdivisionList.stream()
									.collect(Collectors.joining("','", "'", "'"));
							inSubdivisions = " (jsonb_extract_path_text(data,'division','subdivision','id') in ("
									+ commaSeparatedSubdivisionList + ")) ";
						}
						if (null != inNullSubdivisions) {
							// Appending null subdivision in inSubdivisions predicate
							if (null == inSubdivisions) {
								inSubdivisions = inNullSubdivisions;
							} else {
								inSubdivisions = " ( " + inSubdivisions + " or " + inNullSubdivisions + " ) ";
							}
						}
						// Appending division & subdivision with AND in predicate
						inDivSubdivs = " ( " + inDivisions + " and " + inSubdivisions + " ) ";
						if (null == inDivSubdivConsolidated) {
							inDivSubdivConsolidated = " and ( " + inDivSubdivs + " ) ";
						} else {
							inDivSubdivConsolidated = inDivSubdivConsolidated + " or " + inDivSubdivs;
							// inDivSubdivConsolidated = " and ( " + inDivSubdivConsolidated + " or " +
							// inDivSubdivs + " ) ";
						}
					}

				}
				if (!onlyDivisions.isEmpty()) {
					String commaSeparatedinDivisions = onlyDivisions.stream()
							.collect(Collectors.joining("','", "'", "'"));
					String inDivisionsOnly = " (jsonb_extract_path_text(data,'division','id') in ("
							+ commaSeparatedinDivisions + ")) ";
					if (null == inDivSubdivConsolidated) {
						inDivSubdivConsolidated = " and ( " + inDivisionsOnly + " ) ";
					} else {
						inDivSubdivConsolidated = inDivSubdivConsolidated + " or " + inDivisionsOnly;
						// inDivSubdivConsolidated = " and ( " + inDivSubdivConsolidated + " or " +
						// inDivisionsOnly + " ) ";
					}
				}
			}
			return inDivSubdivConsolidated;
		}
		return "";
	}
	
	public String buildPredicateString(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions
			, List<String> locations, List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions
			, List<String> searchTerms,List<String> tags,List<String> relatedProducts) {
		
		return 
				getPublishPredicateString(published,userId,isAdmin) + "\n" + getDivisionsPredicateString(divisions) + "\n" +
				getPhasesPredicateString(phases) + "\n" + getDataVolumesPredicateString(dataVolumes) + "\n" +
				getProjectStatusesPredicateString(statuses) + "\n" + getLocationsPredicateString(locations) + "\n" +
				getSearchTermsPredicateString(searchTerms) + "\n" + getTagsPredicateString(tags) + "\n" +
				getRelatedProductsPredicateString(relatedProducts) + "\n" + getSolutionTypePredicateString(solutionType,userId,bookmarkedSolutions) + "\n" ;
	}

	@Override
	public List<MilestoneWidgetVO> getSolMilestone(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		Query q = getNativeQueryWithFilters(" select cast ( data->'currentPhase' as text), count(*)   ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				"", " group by (data->'currentPhase') ");
		ObjectMapper mapper = new ObjectMapper();
		List<Object[]> results = q.getResultList();
		List<MilestoneWidgetVO> convertedResults = results.stream().map(temp -> {
			MilestoneWidgetVO vo = new MilestoneWidgetVO();
			try {
				String jsonData = temp[0] != null ? temp[0].toString() : "";
				SolutionPhaseVO phase = mapper.readValue(jsonData, SolutionPhaseVO.class);
				vo.setPhase(phase);
			}catch(Exception e) {
				e.printStackTrace();
			}
			BigInteger countResults = (BigInteger) temp[1];
			long count = countResults != null ? countResults.longValue() : 0;
			vo.setSolutionCount(count);
            return vo;
        }).collect(Collectors.toList());
		return convertedResults;
	}
	
	@Override
	public List<LocationWidgetVO> getSolutionLocations(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		Query q = getNativeQueryWithFilters(" select cast (jsonb_array_elements(data->'locations') as text) , count(*) ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				"", " group by jsonb_array_elements(data->'locations') ");
		ObjectMapper mapper = new ObjectMapper();
		List<Object[]> results = q.getResultList();
		List<LocationWidgetVO> convertedResults = results.stream().map(temp -> {
			LocationWidgetVO vo = new LocationWidgetVO();
			try {
				String jsonData = temp[0] != null ? temp[0].toString() : "";
				SolutionLocationVO location = mapper.readValue(jsonData, SolutionLocationVO.class);
				vo.setLocation(location);
			}catch(Exception e) {
				e.printStackTrace();
			}
			BigInteger countResults = (BigInteger) temp[1];
			long count = countResults != null ? countResults.longValue() : 0;
			vo.setSolutionCount(count);
            return vo;
        }).collect(Collectors.toList());
		return convertedResults;
	}
	
	@Override
	public List<DatasourceWidgetVO> getSolutionDataVolume(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		Query q = getNativeQueryWithFilters(" select cast (data->'totalDataVolume' as text) , count(*)  ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				"", " group by (data->'totalDataVolume') ");
		
		ObjectMapper mapper = new ObjectMapper();
		List<Object[]> results = q.getResultList();
		List<DatasourceWidgetVO> convertedResults = results.stream().map(temp -> {
			DatasourceWidgetVO vo = new DatasourceWidgetVO();
			try {
				String jsonData = temp[0] != null ? temp[0].toString() : "";
				DataVolumeVO dataVol = mapper.readValue(jsonData, DataVolumeVO.class);
				vo.setDataVolume(dataVol);
			}catch(Exception e) {
				e.printStackTrace();
			}
			BigInteger countResult = (BigInteger) temp[1];
			long count = temp[1] != null ? countResult.longValue() : 0;
			vo.setSolutionCount(count);
            return vo;
        }).collect(Collectors.toList());
		return convertedResults;
	}
	
	@Override
	public BigDecimal getDigitalValuesSum(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		Query q = getNativeQueryWithFilters(" select sum(cast (data->'digitalValueDetails'->>'digitalValue' as decimal)) ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				"", "");
		BigDecimal result = (BigDecimal) q.getSingleResult();
		return result;
	}
	
	@Override
	public Long getSolCountWithNotebook(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		Query q = getNativeQueryWithFilters("select count(*)  ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				" and jsonb_extract_path_text(data,'dnaNotebookId') is not null \n", "");
		BigInteger result = (BigInteger) q.getSingleResult();
		return result!=null ? result.longValue() : 0;
	}
			
	@Override
	public Long getCountUsingNativeQuery(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		LOGGER.info("Entering getCount.");
		Query q = getNativeQueryWithFilters("select count(*) ", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, new ArrayList<>(), 0, 0, "productName", "asc",
				"", "");
		BigInteger results = (BigInteger) q.getSingleResult();
		return results.longValue();
	}
	
	@Override
	public List<SolutionNsql> getAllWithFiltersUsingNativeQuery(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,List<String> tags,
			List<String> relatedProducts, int offset, int limit,String sortBy, String sortOrder){
		Query q = getNativeQueryWithFilters("", published, phases, dataVolumes, divisions, locations, statuses,
				solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, relatedProducts, offset, limit, sortBy, sortOrder,
				"", "");
		ObjectMapper mapper = new ObjectMapper();
		List<Object[]> results = q.getResultList();
		List<SolutionNsql> convertedResults = results.stream().map(temp -> {
			SolutionNsql entity = new SolutionNsql();
			try {
				String jsonData = temp[1] != null ? temp[1].toString() : "";
				Solution tempSol = mapper.readValue(jsonData, Solution.class);
			entity.setData(tempSol);
			}catch(Exception e) {
				e.printStackTrace();
			}
			String id = temp[0] != null ? temp[0].toString() : "";
			entity.setId(id);
            return entity;
        }).collect(Collectors.toList());
		return convertedResults;
	}
	
	public Query getNativeQueryWithFilters(String selectFieldsString, 
			Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,List<String> tags,
			List<String> relatedProducts, int offset, int limit,String sortBy, String sortOrder, String additionalPredicatesString, String groupByString) {
	
		String prefix = selectFieldsString!=null && !"".equalsIgnoreCase(selectFieldsString) ? selectFieldsString : 
				"select cast(id as text), cast(data as text) ";
		prefix = prefix + "from solution_nsql";
		String basicpredicate = " where (id is not null)";
		String consolidatedPredicates = buildPredicateString(published, phases, dataVolumes, divisions, locations,
				statuses, solutionType, userId, isAdmin, bookmarkedSolutions, searchTerms, tags, relatedProducts);
		String query = prefix + basicpredicate + consolidatedPredicates;
		String sortQueryString = "";
		if(sortBy != null && "".equalsIgnoreCase(sortBy)) {
			switch(sortBy) {
			case  "productName" : sortQueryString = " order by lower(jsonb_extract_path_text(data,'productName')) "; break;
			case  "currentPhase" : sortQueryString = " order by lower(jsonb_extract_path_text(data,'currentPhase','name')) "; break;
			case  "division" : sortQueryString = " order by lower(jsonb_extract_path_text(data,'division','name')) "; break;
			case  "projectStatus" : sortQueryString = " order by lower(jsonb_extract_path_text(data,'projectStatus','name')) "; break;
			case  "digitalValue" : sortQueryString = " order by (jsonb_extract_path_text(data,'digitalValueDetails','digitalValue')) "; break;
			default : sortQueryString = ""; break;
		}
			if("desc".equalsIgnoreCase(sortOrder))
				sortQueryString = sortQueryString + " desc ";
			else
				sortQueryString = sortQueryString + " asc ";
		
			query = query + sortQueryString;
			
		}
		if(additionalPredicatesString!= null && !"".equalsIgnoreCase(additionalPredicatesString))
			query = query + " " + additionalPredicatesString + " \n";
		if(groupByString!= null && !"".equalsIgnoreCase(groupByString))
			query = query + " " + groupByString + " \n";
	    if(limit>0 && !"locations".equalsIgnoreCase(sortBy))
	    	query = query + " limit " + limit;
	    if(offset >= 0 && !"locations".equalsIgnoreCase(sortBy))
			query = query + " offset " + offset;
		Query q = em.createNativeQuery(query);
		return q;
	}
	
	@Override
	public List<SolutionNsql> getAllWithFilters(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
												List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,List<String> tags,
												List<String> relatedProducts, int offset, int limit,String sortBy, String sortOrder) {
		LOGGER.info("Entering getAllWithFilters.");
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<SolutionNsql> cq = cb.createQuery(SolutionNsql.class);
	    Root<SolutionNsql> root = cq.from(SolutionNsql.class);
	    CriteriaQuery<SolutionNsql> getAll = cq.select(root);
	    
	    Predicate consolidatedPredicate = buildPredicate(cb,root,published,phases,dataVolumes,divisions,locations,statuses,
	    		solutionType,userId,isAdmin,bookmarkedSolutions,searchTerms,tags,relatedProducts); 
	    
	    cq.where(consolidatedPredicate);
	    Object[] literalExpressionAndProperties = this.getLiteralForVariable(cb,root,sortBy);
	    Expression<?>[] sortByExpressions = (Expression<?>[]) literalExpressionAndProperties[0];
	    boolean isNumber = (boolean) literalExpressionAndProperties[1];
	    if(sortBy!=null && !"locations".equalsIgnoreCase(sortBy)){
	    	Expression<?> sortFunctionExpression = isNumber ?
	    			cb.function("jsonb_extract_path", BigDecimal.class, sortByExpressions) :
	    				cb.lower(cb.function("jsonb_extract_path_text", String.class, sortByExpressions));
	    	if(sortOrder.equals("asc")) {
					cq.orderBy(cb.asc(sortFunctionExpression));
			}else{
					cq.orderBy(cb.desc(sortFunctionExpression));
			}
		}else {
	    	//default sort asc on solution name
			cq.orderBy(cb.asc(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("productName")))));
		}
	    TypedQuery<SolutionNsql> getAllQuery = em.createQuery(getAll);
	    if(offset >= 0 && !"locations".equalsIgnoreCase(sortBy))
	    	getAllQuery.setFirstResult(offset);
	    if(limit>0 && !"locations".equalsIgnoreCase(sortBy))
	    	getAllQuery.setMaxResults(limit);
	    LOGGER.info("Returning from getAllWithFilters.");
	    return getAllQuery.getResultList();
	}

	private Object[] getLiteralForVariable(CriteriaBuilder cb, Root<SolutionNsql> root, String inputVariable) {
		List<Expression<?>> expressions = new ArrayList<>();
		boolean isNumber = false;
		if(inputVariable != null) {
			switch(inputVariable) {
				case  "productName" : expressions.add(root.get("data")); expressions.add(cb.literal("productName")); break;
				case  "currentPhase" : expressions.add(root.get("data")); expressions.add(cb.literal("currentPhase"));expressions.add(cb.literal("name")); break;
				case  "division" : expressions.add(root.get("data")); expressions.add(cb.literal("division"));expressions.add(cb.literal("name")); break;
				case  "projectStatus" : expressions.add(root.get("data")); expressions.add(cb.literal("projectStatus"));expressions.add(cb.literal("name")); break;
				case  "digitalValue" : expressions.add(root.get("data")); expressions.add(cb.literal("digitalValueDetails"));expressions.add(cb.literal("digitalValue")); isNumber = true; break;
				default : break;
			}
		}
		return new Object[]{expressions.toArray(new Expression[expressions.size()]),isNumber};
	}
	
	@Override
	public Long getCount(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,
			List<String> tags) {
		LOGGER.info("Entering getCount.");
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
	    Root<SolutionNsql> root = cq.from(SolutionNsql.class);
	    CriteriaQuery<Long> getAll = cq.select(cb.count(root));
	    Predicate consolidatedPredicate = buildPredicate(cb,root,published,phases,dataVolumes,divisions,locations,statuses,solutionType,userId,isAdmin,bookmarkedSolutions,searchTerms,tags,null); 
	    cq.where(consolidatedPredicate);
	    TypedQuery<Long> getAllQuery = em.createQuery(getAll);
	    LOGGER.info("Returning from getAllWithFilters.");
	    return getAllQuery.getSingleResult();
	}
	
	
	private Predicate buildPredicate(CriteriaBuilder cb, Root<SolutionNsql> root, Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisionsList,
			List<String> locations, List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,List<String> tags,List<String> relatedProducts) {
		LOGGER.info("Entering buildPredicate.");
		Predicate pMain = cb.isNotNull(root.get("id"));
		
		List<String> allTrueValues = new ArrayList<>();
		allTrueValues.add(Boolean.toString(true));
		Predicate allTrueCondition = cb.function("jsonb_extract_path_text", Boolean.class, root.get("data"),cb.literal("publish")).in(allTrueValues);
		List<String> allFalseValues = new ArrayList<>();
		allFalseValues.add(Boolean.toString(false));
		Predicate allFalseCondition = cb.function("jsonb_extract_path_text", Boolean.class, root.get("data"),cb.literal("publish")).in(allFalseValues);
		Predicate isCreator = null;
		Predicate userCreatedDraftsOnly  = null;
		if(userId!=null) {
			 isCreator = cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("createdBy"), cb.literal("id"))), "%" + userId.toLowerCase() + "%");
			 userCreatedDraftsOnly = cb.and(allFalseCondition, isCreator);
		}
		if(published != null) {
			if(published || isAdmin) {
				Predicate requestedPublishState = published ? allTrueCondition : allFalseCondition;
				pMain = cb.and(pMain,requestedPublishState);
			} 
			if(!published && !isAdmin && userCreatedDraftsOnly!=null){
				pMain = cb.and(pMain,userCreatedDraftsOnly);
			}
		}else {
			if(!isAdmin && userCreatedDraftsOnly!=null) {
			Predicate requestedPublishState = cb.or(allTrueCondition,userCreatedDraftsOnly);
			pMain = cb.and(pMain,requestedPublishState);
			}
		}
		if(published!=null) {
			List<String> publishValues = new ArrayList<>();
			publishValues.add(Boolean.toString(published));
			Predicate isPublished = cb.function("jsonb_extract_path_text", Boolean.class, root.get("data"),cb.literal("publish")).in(publishValues);
			pMain = cb.and(pMain,isPublished);
		}
		if(phases != null && !phases.isEmpty()) {
			Predicate inPhases = cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("currentPhase"), cb.literal("id"))
	        .in(phases);
			pMain = cb.and(pMain,inPhases);
		}
		if(dataVolumes != null && !dataVolumes.isEmpty()) {
			Predicate inDataVolumes = cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("totalDataVolume"), cb.literal("id"))
	        .in(dataVolumes);
			pMain = cb.and(pMain,inDataVolumes);
		}
		//if(divisions != null && !divisions.isEmpty()) {
		//	Predicate inDivisions = cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("division"), cb.literal("id"))
	    //    .in(divisions);
		//	pMain = cb.and(pMain,inDivisions);
		//}
		
		if(locations != null && !locations.isEmpty()) {
			Predicate anyLocationsConsolidate = null;
			for(String location : locations) {
			Predicate tempLocation =  cb.like(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("locations")), "%\""+location+"\"%");
			if(anyLocationsConsolidate==null)
				anyLocationsConsolidate = tempLocation;
			else
				anyLocationsConsolidate = cb.or(anyLocationsConsolidate,tempLocation);
			}
			pMain = cb.and(pMain,anyLocationsConsolidate);
		}
		if(statuses != null && !statuses.isEmpty()) {
			Predicate inStatuses = cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("projectStatus"), cb.literal("id"))
	        .in(statuses);
			pMain = cb.and(pMain,inStatuses);
		}
		if(solutionType!=null && !"".equalsIgnoreCase(solutionType)) {
			if("1".equalsIgnoreCase(solutionType)) {
				if(bookmarkedSolutions == null || bookmarkedSolutions.isEmpty()) {
					bookmarkedSolutions.add(noBookMarkId);
				}
					Expression<String> idListExpression = root.get("id");
					Predicate inbookmarkedSolutions = idListExpression.in(bookmarkedSolutions);
					pMain = cb.and(pMain,inbookmarkedSolutions);
				
			}
			if("2".equalsIgnoreCase(solutionType)){
				userId = userId.toLowerCase();
				Predicate isMember =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("teamMembers"))), "%"+userId+"%");
				Predicate currentUser = cb.or(isMember,isCreator);
				pMain = cb.and(pMain,currentUser);
			}
		}
		
		if(searchTerms != null && !searchTerms.isEmpty()) {
			
			Predicate anySearchTermConsolidate = null;
			for(String searchTerm : searchTerms) {
				searchTerm = searchTerm.toLowerCase();
				Predicate tempProductNameCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("productName"))), "%"+searchTerm+"%");
				Predicate tempTagCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("tags"))), "%"+searchTerm+"%");
				Predicate tempDSCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("dataSources"))), "%"+searchTerm+"%");
				Predicate tempPlatformCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("platforms"))), "%"+searchTerm+"%");
				Predicate tempLangCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("languages"))), "%"+searchTerm+"%");
				Predicate tempAlgoCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("algorithms"))), "%"+searchTerm+"%");
				Predicate tempVisualizationCondition =  cb.like(cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("visualizations"))), "%"+searchTerm+"%");
				Predicate consolidateTempKeyCondition = cb.or(tempProductNameCondition,tempTagCondition,tempDSCondition,tempPlatformCondition,tempLangCondition,tempAlgoCondition,tempVisualizationCondition);
				if(anySearchTermConsolidate==null)
					anySearchTermConsolidate = consolidateTempKeyCondition;
				else
					anySearchTermConsolidate = cb.or(anySearchTermConsolidate,consolidateTempKeyCondition);
			}
			pMain = cb.and(pMain,anySearchTermConsolidate);
			
		}
		if (tags != null && !tags.isEmpty()) {
			Predicate anySearchTagConsolidate = null;
			for(String tag : tags) {
				Predicate tempTagCondition = cb.like(
						cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("tags")),
						"%" + tag + "%");
			Predicate consolidateTempKeyCondition = cb.or(tempTagCondition);
			if (anySearchTagConsolidate == null)
				anySearchTagConsolidate = consolidateTempKeyCondition;
			else
				anySearchTagConsolidate = cb.or(anySearchTagConsolidate, consolidateTempKeyCondition);
			}
			
			pMain = cb.and(pMain, anySearchTagConsolidate);
		}
		//RelatedProducts as predicate List<String> relatedProducts
		if (relatedProducts != null && !relatedProducts.isEmpty()) {
			Predicate anySearchRelatedProductConsolidate = null;
			for(String relatedProduct : relatedProducts) {
				Predicate tempRelatedProductCondition = cb.like(
						cb.function("jsonb_extract_path_text", String.class, root.get("data"), cb.literal("relatedProducts")),
						"%" + relatedProduct + "%");
			Predicate consolidateTempKeyCondition = cb.or(tempRelatedProductCondition);
			if (anySearchRelatedProductConsolidate == null)
				anySearchRelatedProductConsolidate = consolidateTempKeyCondition;
			else
				anySearchRelatedProductConsolidate = cb.or(anySearchRelatedProductConsolidate, consolidateTempKeyCondition);
			}
			
			pMain = cb.and(pMain, anySearchRelatedProductConsolidate);
		}
		//Adding division & subdivision as predicate
		if (null != divisionsList && !divisionsList.isEmpty()) {
			List<String> onlyDivisions = new ArrayList<String>();
			Predicate inDivSubdivConsolidated = null;
			for (Map<String, List<String>> divSubdivMap : divisionsList) {
				for (String div : divSubdivMap.keySet()) {
					Predicate inDivSubdivs = null;
					if (null == divSubdivMap.get(div) || divSubdivMap.get(div).isEmpty()) {
						onlyDivisions.add(div);
					} else {
						// Adding Division in predicate
						Predicate inDivisions = cb.function("jsonb_extract_path_text", String.class, root.get("data"),
								cb.literal("division"), cb.literal("id")).in(Arrays.asList(div));

						List<String> subdivisionList = divSubdivMap.get(div);
						Predicate inNullSubdivisions = null;
						if (subdivisionList.contains(ConstantsUtility.EMPTY_VALUE)) {
							// adding null subdivision in predicate
							inNullSubdivisions = cb
									.function("jsonb_extract_path_text", String.class, root.get("data"),
											cb.literal("division"), cb.literal("subdivision"), cb.literal("id"))
									.isNull();
						}

						Predicate inSubdivisions = null;
						if (!subdivisionList.isEmpty()) {
							// Adding subdivisions in predicate
							inSubdivisions = cb
									.function("jsonb_extract_path_text", String.class, root.get("data"),
											cb.literal("division"), cb.literal("subdivision"), cb.literal("id"))
									.in(subdivisionList);
						}
						if (null != inNullSubdivisions) {
							// Appending null subdivision in inSubdivisions predicate
							if (null == inSubdivisions) {
								inSubdivisions = inNullSubdivisions;
							} else {
								inSubdivisions = cb.or(inSubdivisions, inNullSubdivisions);
							}
						}
						// Appending division & subdivision with AND in predicate
						inDivSubdivs = cb.and(inDivisions, inSubdivisions);

						if (null == inDivSubdivConsolidated) {
							inDivSubdivConsolidated = inDivSubdivs;
						} else {
							inDivSubdivConsolidated = cb.or(inDivSubdivConsolidated, inDivSubdivs);
						}
					}

				}
				if (!onlyDivisions.isEmpty()) {
					Predicate inDivisionsOnly = cb.function("jsonb_extract_path_text", String.class, root.get("data"),
							cb.literal("division"), cb.literal("id")).in(onlyDivisions);
					if (null == inDivSubdivConsolidated) {
						inDivSubdivConsolidated = inDivisionsOnly;
					} else {
						inDivSubdivConsolidated = cb.or(inDivSubdivConsolidated, inDivisionsOnly);
					}
				}
			}
			pMain = cb.and(pMain, inDivSubdivConsolidated);
		}
		LOGGER.info("Returning from buildPredicate.");
		return pMain;
	}

}