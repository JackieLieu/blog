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
package me.qyh.blog.comment.module;

import com.google.gson.annotations.Expose;

import me.qyh.blog.comment.base.BaseComment;

/**
 * 一种类似页面性质的评论，
 * 
 * @author Administrator
 *
 */
public class ModuleComment extends BaseComment<ModuleComment> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose(serialize = false, deserialize = true)
	private CommentModule module;

	public CommentModule getModule() {
		return module;
	}

	public void setModule(CommentModule module) {
		this.module = module;
	}

	@Override
	public boolean matchParent(ModuleComment parent) {
		return super.matchParent(parent) && this.module.equals(parent.module);
	}

}
