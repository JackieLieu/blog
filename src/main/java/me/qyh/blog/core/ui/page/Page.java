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
package me.qyh.blog.core.ui.page;

import java.util.Objects;

import me.qyh.blog.core.entity.BaseEntity;
import me.qyh.blog.core.entity.Space;
import me.qyh.blog.core.exception.SystemException;
import me.qyh.blog.core.ui.Template;
import me.qyh.blog.util.Validators;

public class Page extends BaseEntity implements Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Space space;// 所属空间
	private String tpl;// 组织片段，用来将片段组织在一起
	private PageType type;

	public enum PageType {
		SYSTEM, USER, LOCK
	}

	public Page() {
		super();
	}

	public Page(Integer id) {
		super(id);
	}

	public Page(Space space) {
		this.space = space;
	}

	public Page(Page page) {
		this.id = page.id;
		this.space = page.space;
		this.tpl = page.tpl;
		this.type = page.type;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public void setTpl(String tpl) {
		this.tpl = tpl;
	}

	public String getTpl() {
		return tpl;
	}

	public PageType getType() {
		return type;
	}

	public void setType(PageType type) {
		this.type = type;
	}

	public Page toExportPage() {
		Page page = new Page();
		page.setTpl(tpl);
		page.setType(type);
		return page;
	}

	@Override
	public final boolean isRoot() {
		return true;
	}

	@Override
	public final String getTemplate() {
		return tpl;
	}

	@Override
	public String getTemplateName() {
		throw new SystemException("无法获取页面模板名");
	}

	@Override
	public Template cloneTemplate() {
		return new Page(this);
	}

	@Override
	public final boolean isCallable() {
		return false;
	}

	@Override
	public boolean equalsTo(Template other) {
		if (Validators.baseEquals(this, other)) {
			Page rhs = (Page) other;
			return Objects.equals(space, rhs.space) && Objects.equals(tpl, rhs.tpl) && Objects.equals(type, rhs.type);
		}
		return false;
	}
}
