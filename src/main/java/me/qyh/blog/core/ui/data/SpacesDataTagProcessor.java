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
package me.qyh.blog.core.ui.data;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import me.qyh.blog.core.entity.Space;
import me.qyh.blog.core.exception.LogicException;
import me.qyh.blog.core.pageparam.SpaceQueryParam;
import me.qyh.blog.core.security.Environment;
import me.qyh.blog.core.service.SpaceService;

/**
 * 查询所有的空间
 * 
 * @author mhlx
 *
 */
public class SpacesDataTagProcessor extends DataTagProcessor<List<Space>> {

	@Autowired
	private SpaceService spaceService;

	public SpacesDataTagProcessor(String name, String dataName) {
		super(name, dataName);
	}

	@Override
	protected List<Space> buildPreviewData(Attributes attributes) {
		return Arrays.asList(getSpace());
	}

	@Override
	protected List<Space> query(Attributes attributes) throws LogicException {
		SpaceQueryParam param = new SpaceQueryParam();
		param.setQueryPrivate(Environment.isLogin());
		return spaceService.querySpace(param);
	}

}
