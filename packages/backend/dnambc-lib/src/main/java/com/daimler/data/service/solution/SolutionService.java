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

package com.daimler.data.service.solution;

import com.daimler.data.db.entities.SolutionNsql;
import com.daimler.data.dto.solution.ChangeLogVO;
import com.daimler.data.dto.solution.SolutionVO;
import com.daimler.data.service.common.CommonService;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface SolutionService
					extends CommonService<SolutionVO, SolutionNsql, String>{

	enum TAG_CATEGORY{
		TAG,DS,PLATFORM,LANG,ALGO,VISUALIZATION,RELATEDPRODUCT;
	}

	List<SolutionVO> getAllWithFilters(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions, List<String> locations,
			List<String> statuses, String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms,List<String> tags, int offset, int limit,String sortBy, String sortOrder);

	Long getCount(Boolean published, List<String> phases, List<String> dataVolumes, List<Map<String, List<String>>> divisions,
			List<String> locations, List<String> statuses, 
			String solutionType, String userId, Boolean isAdmin, List<String> bookmarkedSolutions, List<String> searchTerms, List<String> tags);

	void deleteTagForEachSolution(String tagName, String relatedProductName, TAG_CATEGORY category);

	void deleteInActiveSolutionsOlderThan(Calendar startDate);
	
	List<ChangeLogVO>  getChangeLogsBySolutionId(String id);
}
