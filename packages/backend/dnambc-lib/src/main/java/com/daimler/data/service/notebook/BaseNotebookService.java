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

package com.daimler.data.service.notebook;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daimler.data.assembler.NotebookAssembler;
import com.daimler.data.controller.NotebookController;
import com.daimler.data.db.entities.NotebookNsql;
import com.daimler.data.db.jsonb.Notebook;
import com.daimler.data.db.repo.notebook.NotebookCustomRepository;
import com.daimler.data.db.repo.notebook.NotebookRepository;
import com.daimler.data.dto.notebook.NotebookVO;
import com.daimler.data.service.common.BaseCommonService;

@Service
public class BaseNotebookService
        extends BaseCommonService<NotebookVO, NotebookNsql, String>
        implements NotebookService {

	private static Logger LOGGER = LoggerFactory.getLogger(BaseNotebookService.class);
	
    @Autowired
    private NotebookCustomRepository customRepo;
    @Autowired
    private NotebookRepository jpaRepo;
    @Autowired
    private NotebookAssembler notebookAssembler;

    public BaseNotebookService() {
        super();
    }
    

    /**
     * updateSolutionIdofDnaNotebook
     * <p> update solution id of DnA Notebook
     * 
     * @param dnaNotebookId
     * @param solutionId
     */
	@Override
	@Transactional
	public void updateSolutionIdofDnaNotebook(String dnaNotebookId, String solutionId) {
		LOGGER.trace("Start updateSolutionIdofDnaNotebook...");
		
//		Optional<NotebookNsql> notebookOptional= jpaRepo.findById(dnaNotebookId);
//		NotebookNsql notebookNsql = (notebookOptional != null && !notebookOptional.isEmpty()) ? notebookOptional.get()
//				: null;
//		if(notebookNsql!=null && notebookNsql.getData()!=null) {
//			notebookNsql.getData().setSolutionId(solutionId);
//			LOGGER.debug("Saving Notebook...");
//			jpaRepo.save(notebookNsql);
//		}
		
		if(solutionId!=null) {
			NotebookNsql existingNotebook = customRepo.findbyUniqueLiteral("solutionId", solutionId);
			if(existingNotebook!=null) {
				Notebook existingNotebookDetails = existingNotebook.getData();
				if(dnaNotebookId!=null && !"".equals(dnaNotebookId)) {
					String preNotebookId = existingNotebook.getId();
					if(!preNotebookId.equalsIgnoreCase(dnaNotebookId)) {
						updateNotebook(null, existingNotebook);
						LOGGER.debug("Solution {} unlinked from old notebook {} ",solutionId,existingNotebook.getId() );
						Optional<NotebookNsql> notebookOptional = jpaRepo.findById(dnaNotebookId);
						NotebookNsql notebookNsql = (notebookOptional != null && !notebookOptional.isEmpty()) ? notebookOptional.get(): null;
						if(notebookNsql!=null) {
							updateNotebook(solutionId, notebookNsql);
							LOGGER.debug("Solution {} linked to notebook {} ",solutionId, notebookNsql.getId());
						}
					}
				}else {
					updateNotebook(null, existingNotebook);
					LOGGER.debug("Solution {} unlinked from notebook {} ",solutionId, existingNotebook.getId());
				}
			}else {
				if(dnaNotebookId!=null) {
					Optional<NotebookNsql> notebookOptional = jpaRepo.findById(dnaNotebookId);
					NotebookNsql notebookNsql = (notebookOptional != null && !notebookOptional.isEmpty()) ? notebookOptional.get(): null;
					if(notebookNsql!=null) {
						updateNotebook(solutionId, notebookNsql);
						LOGGER.debug("Solution {} linked to notebook {} ",solutionId, notebookNsql.getId());
					}
				}
			}
		}else {
			if(dnaNotebookId!=null) {
				Optional<NotebookNsql> notebookOptional = jpaRepo.findById(dnaNotebookId);
				NotebookNsql notebookNsql = (notebookOptional != null && !notebookOptional.isEmpty()) ? notebookOptional.get(): null;
				if(notebookNsql!=null) {
					updateNotebook(null, notebookNsql);
					LOGGER.debug("Solution {} unlinked from notebook {} , on delete of solution",solutionId, notebookNsql.getId());
				}
			}
		}
		LOGGER.trace("Returning from updateSolutionIdofDnaNotebook...");
	}

	private void updateNotebook(String solutionId, NotebookNsql notebookRecord) {
    	Notebook existingNotebookDetails = notebookRecord.getData();
    	existingNotebookDetails.setSolutionId(solutionId);
    	notebookRecord.setData(existingNotebookDetails);
		customRepo.update(notebookRecord);
    }
    

}
