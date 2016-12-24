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
package me.qyh.blog.service;

import java.util.List;

import me.qyh.blog.bean.ArticleDateFiles;
import me.qyh.blog.bean.ArticleDateFiles.ArticleDateFileMode;
import me.qyh.blog.bean.ArticleNav;
import me.qyh.blog.bean.ArticleSpaceFile;
import me.qyh.blog.bean.ArticleStatistics;
import me.qyh.blog.bean.TagCount;
import me.qyh.blog.entity.Article;
import me.qyh.blog.entity.Space;
import me.qyh.blog.exception.LogicException;
import me.qyh.blog.metaweblog.MetaweblogArticle;
import me.qyh.blog.pageparam.ArticleQueryParam;
import me.qyh.blog.pageparam.PageResult;
import me.qyh.blog.security.AuthencationException;

/**
 * 
 * @author Administrator
 *
 */
public interface ArticleService {

	/**
	 * 获取一篇可以被访问的文章
	 * 
	 * @param idOrAlias
	 *            id或者文章别名
	 * @throws AuthencationException
	 *             如果访问了私人博客但是没有登录
	 * @return 不存在|不可被访问 null
	 */
	Article getArticleForView(String idOrAlias);

	/**
	 * 获取一篇可以被编辑的文章
	 * 
	 * @param id
	 *            文章id
	 * @throws LogicException
	 * @return 文章
	 */
	Article getArticleForEdit(Integer id) throws LogicException;

	/**
	 * 查询文章日期归档
	 * 
	 * @param space
	 *            空间
	 * @param mode
	 *            归档方式
	 * @return 文章归档
	 */
	ArticleDateFiles queryArticleDateFiles(Space space, ArticleDateFileMode mode);

	/**
	 * 查询文章空间归档
	 * 
	 * @return 文章空间归档集合
	 */
	List<ArticleSpaceFile> queryArticleSpaceFiles();

	/**
	 * 分页查询文章
	 * 
	 * @param param
	 *            查询参数
	 * @return 文章分页对象
	 */
	PageResult<Article> queryArticle(ArticleQueryParam param);

	/**
	 * 发表要发表的计划博客
	 * 
	 * @return 成功发表的数量
	 * 
	 */
	int pushScheduled();

	/**
	 * 插入|更新 文章
	 * 
	 * @param article
	 *            文章
	 * @param autoDraft
	 *            是否是自动保存的草稿
	 * @return 插入后的文章
	 * @throws LogicException
	 */
	Article writeArticle(Article article, boolean autoDraft) throws LogicException;

	/**
	 * 将博客放入回收站
	 * 
	 * @param id
	 *            文章id
	 * @throws LogicException
	 */
	void logicDeleteArticle(Integer id) throws LogicException;

	/**
	 * 从回收站中恢复
	 * 
	 * @param id
	 *            文章id
	 * @throws LogicException
	 */
	void recoverArticle(Integer id) throws LogicException;

	/**
	 * 删除博客
	 * 
	 * @param id
	 *            文章id
	 * @throws LogicException
	 */
	void deleteArticle(Integer id) throws LogicException;

	/**
	 * 增加文章点击数
	 * 
	 * @param id
	 *            文章id
	 * @return 被点击的文章
	 */
	Article hit(Integer id);

	/**
	 * 发布草稿
	 * 
	 * @param id
	 *            草稿id
	 * @throws LogicException
	 */
	void publishDraft(Integer id) throws LogicException;

	/**
	 * 上一篇，下一篇文章
	 * 
	 * @param article
	 *            当前文章
	 * @return 当前文章的上一篇下一篇，如果都没有，返回null
	 */
	ArticleNav getArticleNav(Article article);

	/**
	 * 上一篇，下一篇文章
	 * 
	 * @param idOrAlias
	 *            文章的id或者别名
	 * @return 当前文章的上一篇下一篇，如果都没有，返回null
	 */
	ArticleNav getArticleNav(String idOrAlias);

	/**
	 * 查询博客统计 <br>
	 * <strong>只会统计状态为发表的博客点击数、评论数、最近撰写日期和最后修改日期</strong>
	 * 
	 * @param space
	 *            空间
	 * @param queryHidden
	 *            是否查询隐藏的文章
	 * @return 文章统计详情
	 */
	ArticleStatistics queryArticleStatistics(Space space, boolean queryHidden);

	/**
	 * 查询被文章所引用的标签集
	 * 
	 * @param space
	 *            空间
	 * @param hasLock
	 *            是否查询锁保护的文章
	 * @param queryPrivate
	 *            是否查询私人文章
	 * @return 标签集
	 */
	List<TagCount> queryTags(Space space, boolean hasLock, boolean queryPrivate);

	/**
	 * 更新metaweblog文章
	 * 
	 * @param article
	 *            metaweblog 撰写的文章
	 * @return 保存后的文章
	 * @throws LogicException
	 */
	Article writeArticle(MetaweblogArticle article) throws LogicException;

	/**
	 * 查询最近的文章
	 * 
	 * @param limit
	 *            最大返回数目限制
	 * @return 最近的文章
	 */
	List<Article> queryRecentArticles(Integer limit);

	/**
	 * 查询类似文章
	 * 
	 * @param idOrAlias
	 *            文章id或者别名
	 * @param limit
	 *            最大数目
	 * @return 类似文章集合
	 * @throws LogicException
	 */
	List<Article> findSimilar(String idOrAlias, int limit) throws LogicException;

	/**
	 * 查询类似文章
	 * 
	 * @param article
	 *            当前文章
	 * @param limit
	 *            最大返回条目
	 * @return 类似文章集合
	 * @throws LogicException
	 */
	List<Article> findSimilar(Article article, int limit) throws LogicException;

}
