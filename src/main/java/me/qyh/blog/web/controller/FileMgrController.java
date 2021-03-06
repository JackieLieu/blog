/*
 * Copyright 2016 qyh.me
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.qyh.blog.web.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import me.qyh.blog.core.bean.FileStoreBean;
import me.qyh.blog.core.bean.JsonResult;
import me.qyh.blog.core.bean.UploadedFile;
import me.qyh.blog.core.entity.BlogFile;
import me.qyh.blog.core.entity.BlogFile.BlogFileType;
import me.qyh.blog.core.exception.LogicException;
import me.qyh.blog.core.file.FileStore;
import me.qyh.blog.core.message.Message;
import me.qyh.blog.core.pageparam.BlogFileQueryParam;
import me.qyh.blog.core.service.FileService;
import me.qyh.blog.web.controller.form.BlogFileQueryParamValidator;
import me.qyh.blog.web.controller.form.BlogFileUpload;
import me.qyh.blog.web.controller.form.BlogFileUploadValidator;
import me.qyh.blog.web.controller.form.BlogFileValidator;

@Controller
@RequestMapping("mgr/file")
public class FileMgrController extends BaseMgrController {

	@Autowired
	private FileService fileService;
	@Autowired
	private BlogFileQueryParamValidator blogFileParamValidator;
	@Autowired
	private BlogFileUploadValidator blogFileUploadValidator;
	@Autowired
	private BlogFileValidator blogFileValidator;

	@InitBinder(value = "blogFileQueryParam")
	protected void initBlogFileQueryParamBinder(WebDataBinder binder) {
		binder.setValidator(blogFileParamValidator);
	}

	@InitBinder(value = "blogFileUpload")
	protected void initBlogUploadBinder(WebDataBinder binder) {
		binder.setValidator(blogFileUploadValidator);
	}

	@InitBinder(value = "blogFile")
	protected void initBlogFileBinder(WebDataBinder binder) {
		binder.setValidator(blogFileValidator);
	}

	@RequestMapping("index")
	public String index(@Validated BlogFileQueryParam blogFileQueryParam, BindingResult result, Model model) {
		if (result.hasErrors()) {
			blogFileQueryParam = new BlogFileQueryParam();
			blogFileQueryParam.setCurrentPage(1);
		}
		blogFileQueryParam.setQuerySubDir(false);
		blogFileQueryParam.setExtensions(new HashSet<>());
		try {
			model.addAttribute("result", fileService.queryBlogFiles(blogFileQueryParam));
			model.addAttribute("stores", fileService.allStorableStores());
		} catch (LogicException e) {
			model.addAttribute(ERROR, e.getLogicMessage());
		}
		return "mgr/file/index";
	}

	@RequestMapping("stores")
	@ResponseBody
	public List<FileStoreBean> allServers() {
		List<FileStore> stores = fileService.allStorableStores();
		return stores.stream().map(FileStoreBean::new).collect(Collectors.toList());
	}

	@RequestMapping("query")
	@ResponseBody
	public JsonResult query(@Validated BlogFileQueryParam blogFileQueryParam, BindingResult result)
			throws LogicException {
		if (result.hasErrors()) {
			blogFileQueryParam = new BlogFileQueryParam();
			blogFileQueryParam.setCurrentPage(1);
		}
		blogFileQueryParam.setQuerySubDir(false);
		blogFileQueryParam.setExtensions(new HashSet<>());
		return new JsonResult(true, fileService.queryBlogFiles(blogFileQueryParam));
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult upload(@Validated BlogFileUpload blogFileUpload, BindingResult result) throws LogicException {
		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for (ObjectError error : errors) {
				return new JsonResult(false,
						new Message(error.getCode(), error.getDefaultMessage(), error.getArguments()));
			}
		}
		List<UploadedFile> uploadedFiles = fileService.upload(blogFileUpload);
		return new JsonResult(true, uploadedFiles);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult delete(@RequestParam("id") Integer id) throws LogicException {
		fileService.delete(id);
		return new JsonResult(true, new Message("file.delete.success", "删除成功"));
	}

	@RequestMapping(value = "{id}/pro", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult pro(@PathVariable("id") int id) throws LogicException {
		return new JsonResult(true, fileService.getBlogFileProperty(id));
	}

	@RequestMapping(value = "createFolder", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult createFolder(@RequestBody @Validated BlogFile blogFile) throws LogicException {
		blogFile.setCf(null);
		blogFile.setType(BlogFileType.DIRECTORY);
		fileService.createFolder(blogFile);
		return new JsonResult(true, new Message("file.create.success", "创建成功"));
	}

	@RequestMapping(value = "copy", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult copy(@RequestParam("sourceId") Integer sourceId, @RequestParam("folderPath") String folderPath)
			throws LogicException {
		Errors bingdingResult = new MapBindingResult(new HashMap<>(), "folderPath");
		BlogFileValidator.validFolderPath(folderPath, bingdingResult);
		if (bingdingResult.hasErrors()) {
			List<ObjectError> errors = bingdingResult.getAllErrors();
			for (ObjectError error : errors) {
				return new JsonResult(false,
						new Message(error.getCode(), error.getDefaultMessage(), error.getArguments()));
			}
		}
		fileService.copy(sourceId, folderPath);
		return new JsonResult(true, new Message("file.copy.success", "拷贝成功"));
	}

	@RequestMapping(value = "move", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult move(@RequestParam("sourceId") Integer sourceId, @RequestParam("destPath") String destPath)
			throws LogicException {
		Errors bingdingResult = new MapBindingResult(new HashMap<>(), "destPath");
		BlogFileValidator.validFilePath(destPath, bingdingResult);
		if (bingdingResult.hasErrors()) {
			List<ObjectError> errors = bingdingResult.getAllErrors();
			for (ObjectError error : errors) {
				return new JsonResult(false,
						new Message(error.getCode(), error.getDefaultMessage(), error.getArguments()));
			}
		}
		fileService.move(sourceId, destPath);
		return new JsonResult(true, new Message("file.move.success", "移动成功"));
	}
}
