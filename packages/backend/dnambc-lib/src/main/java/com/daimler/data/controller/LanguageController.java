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

package com.daimler.data.controller;

import com.daimler.data.api.language.LanguagesApi;
import com.daimler.data.application.auth.UserStore;
import com.daimler.data.controller.exceptions.*;
import com.daimler.data.dto.language.LanguageCollection;
import com.daimler.data.dto.language.LanguageRequestVO;
import com.daimler.data.dto.language.LanguageVO;
import com.daimler.data.dto.solution.CreatedByVO;
import com.daimler.data.dto.userinfo.UserInfoVO;
import com.daimler.data.dto.userinfo.UserRoleVO;
import com.daimler.data.service.language.LanguageService;
import com.daimler.data.service.userinfo.UserInfoService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(value = "Language API", tags = {"languages"})
@RequestMapping("/api")
@Slf4j
public class LanguageController implements LanguagesApi {

    @Autowired
    private UserStore userStore;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LanguageService languageService;

    @Override
    @ApiOperation(value = "Adds a new language.", nickname = "create", notes = "Adds a new non existing language which is used in providing solution.", response = LanguageVO.class, tags = {"languages",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Returns message of succes or failure ", response = LanguageVO.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
            @ApiResponse(code = 403, message = "Request is not authorized."),
            @ApiResponse(code = 405, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal error")})
    @RequestMapping(value = "/languages",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<LanguageVO> create(@ApiParam(value = "Request Body that contains data required for creating a new Language", required = true) @Valid @RequestBody LanguageRequestVO languageRequestVO) {
        LanguageVO requestLanguageVO = languageRequestVO.getData();
        try {
            LanguageVO existingLanguageVO = languageService.getByUniqueliteral("name", requestLanguageVO.getName());
            if (existingLanguageVO != null && existingLanguageVO.getName() != null)
                return new ResponseEntity<>(existingLanguageVO, HttpStatus.CONFLICT);
            requestLanguageVO.setId(null);
            LanguageVO languageVO = languageService.create(requestLanguageVO);
            if (languageVO != null && languageVO.getId() != null) {
                return new ResponseEntity<>(languageVO, HttpStatus.CREATED);
            } else
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @ApiOperation(value = "Deletes the language identified by given ID.", nickname = "delete", notes = "Deletes the language identified by given ID", response = GenericMessage.class, tags = {"languages",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted.", response = GenericMessage.class),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
            @ApiResponse(code = 403, message = "Request is not authorized."),
            @ApiResponse(code = 500, message = "Internal error")})
    @RequestMapping(value = "/languages/{id}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<GenericMessage> delete(@ApiParam(value = "Id of the language", required = true) @PathVariable("id") String id) {
        try {
            CreatedByVO currentUser = this.userStore.getVO();
            String userId = currentUser != null ? currentUser.getId() : "";
            if (userId != null && !"".equalsIgnoreCase(userId)) {
                UserInfoVO userInfoVO = userInfoService.getById(userId);
                if (userInfoVO != null) {
                    List<UserRoleVO> userRoleVOs = userInfoVO.getRoles();
                    if (userRoleVOs != null && !userRoleVOs.isEmpty()) {
                        boolean isAdmin = userRoleVOs.stream().anyMatch(n -> "Admin".equalsIgnoreCase(n.getName()));
                        if (userId == null || !isAdmin) {
                            MessageDescription notAuthorizedMsg = new MessageDescription();
                            notAuthorizedMsg.setMessage("Not authorized to delete Languages. User does not have admin privileges.");
                            GenericMessage errorMessage = new GenericMessage();
                            errorMessage.addErrors(notAuthorizedMsg);
                            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
                        }
                    }
                }
            }
            languageService.deleteLanguage(id);
            GenericMessage successMsg = new GenericMessage();
            successMsg.setSuccess("success");
            return new ResponseEntity<>(successMsg, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            log.error(e.getLocalizedMessage());
            MessageDescription invalidMsg = new MessageDescription("No tag with the given id");
            GenericMessage errorMessage = new GenericMessage();
            errorMessage.addErrors(invalidMsg);
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            MessageDescription exceptionMsg = new MessageDescription("Failed to delete due to internal error.");
            GenericMessage errorMessage = new GenericMessage();
            errorMessage.addErrors(exceptionMsg);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @ApiOperation(value = "Get all available languages.", nickname = "getAll", notes = "Get all languages. This endpoints will be used to Get all valid available languages maintenance records.", response = LanguageCollection.class, tags = {"languages",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully completed fetching all languages", response = LanguageCollection.class),
            @ApiResponse(code = 204, message = "Fetch complete, no content found"),
            @ApiResponse(code = 500, message = "Internal error")})
    @RequestMapping(value = "/languages",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity<LanguageCollection> getAll() {
        final List<LanguageVO> languages = languageService.getAll();
        LanguageCollection languageCollection = new LanguageCollection();
        if (languages != null && languages.size() > 0) {
            languageCollection.addAll(languages);
            return new ResponseEntity<>(languageCollection, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(languageCollection, HttpStatus.NO_CONTENT);
        }
    }


}
