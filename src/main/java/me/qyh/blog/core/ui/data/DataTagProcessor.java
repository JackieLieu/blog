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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.qyh.blog.core.entity.Space;
import me.qyh.blog.core.exception.LogicException;
import me.qyh.blog.core.security.Environment;
import me.qyh.blog.util.Validators;

public abstract class DataTagProcessor<T> {

	/**
	 * 是否忽略逻辑异常
	 */
	private static final String DATA_NAME = "dataName";

	private String name;// 数据名，唯一
	private String dataName;// 默认数据绑定名，唯一
	private boolean callable;// 是否可以被ajax调用

	protected static final Space previewSpace = new Space();

	protected static final Logger LOGGER = LoggerFactory.getLogger(DataTagProcessor.class);

	static {
		previewSpace.setAlias("preview");
		previewSpace.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
		previewSpace.setId(1);
		previewSpace.setIsDefault(false);
		previewSpace.setIsPrivate(false);
		previewSpace.setName("Preview");
		previewSpace.setLockId(null);
	}

	/**
	 * 构造器
	 * 
	 * @param name
	 *            数据处理器名称
	 * @param dataName
	 *            页面dataName
	 */
	public DataTagProcessor(String name, String dataName) {
		this.name = name;
		this.dataName = dataName;
	}

	/**
	 * 查询数据
	 * 
	 * @param variables
	 * @param attributes
	 * @return
	 * @throws LogicException
	 */
	public final DataBind<T> getData(Map<String, String> attributes) throws LogicException {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		Attributes atts = new Attributes(attributes);
		T result = query(atts);
		DataBind<T> bind = new DataBind<>();
		bind.setData(result);
		String dataNameAttV = atts.get(DATA_NAME);
		if (validDataName(dataNameAttV)) {
			bind.setDataName(dataNameAttV);
		} else {
			bind.setDataName(dataName);
		}
		return bind;
	}

	/**
	 * 构造预览用数据
	 * 
	 * @param attributes
	 * @return
	 */
	public final DataBind<T> previewData(Map<String, String> attributes) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		Attributes atts = new Attributes(attributes);
		T result = buildPreviewData(atts);
		DataBind<T> bind = new DataBind<>();
		bind.setData(result);
		String dataNameAttV = atts.get(DATA_NAME);
		if (validDataName(dataNameAttV)) {
			bind.setDataName(dataNameAttV);
		} else {
			bind.setDataName(dataName);
		}
		return bind;
	}

	/**
	 * 获取测试数据
	 * 
	 * @return
	 */
	protected abstract T buildPreviewData(Attributes attributes);

	protected abstract T query(Attributes attributes) throws LogicException;

	public String getName() {
		return name;
	}

	public String getDataName() {
		return dataName;
	}

	public boolean isCallable() {
		return callable;
	}

	public void setCallable(boolean callable) {
		this.callable = callable;
	}

	protected Space getSpace() {
		Space current = Environment.getSpace();
		return current == null ? previewSpace : current;
	}

	protected Space getCurrentSpace() {
		return Environment.getSpace();
	}

	protected final class Attributes {
		private final Map<String, String> attMap;

		public String get(String key) {
			return attMap.get(key);
		}

		public Attributes(Map<String, String> attMap) {
			this.attMap = Collections.unmodifiableMap(attMap);
		}

		public String getOrDefault(String key, String defaultValue) {
			return attMap.getOrDefault(key, defaultValue);
		}

		@Override
		public String toString() {
			return "Attributes [attMap=" + attMap + "]";
		}
	}

	private boolean validDataName(String dataName) {
		return Validators.isLetter(dataName);
	}
}
