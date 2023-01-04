package com.test.newsview.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.test.newsview.api.Api
import com.test.newsview.data.ArticleRepository.Companion.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException

const val STARTING_KEY=1

class ArticlePagingSource(private val query:String?=null, private val country:String?=null, private val category:String?=null) : PagingSource<Int, Article>() {

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = Integer.max(STARTING_KEY, key)

    //Функция load() будет вызвана библиотекой подкачки для асинхронной выборки дополнительных данных,
    // которые будут отображаться по мере прокрутки пользователем.
    // Объект LoadParams хранит информацию, относящуюся к операции загрузки, включая следующее:
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: STARTING_KEY
        return try {
            val response = Api.get().getArticleRepos(query=query, page=position, pageSize=params.loadSize, country=country,category=category)
            val repos = response.items
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == STARTING_KEY) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }


    //// The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPageIndex = state.pages.indexOf(state.closestPageToPosition(anchorPosition))
            state.pages.getOrNull(anchorPageIndex + 1)?.prevKey ?: state.pages.getOrNull(anchorPageIndex - 1)?.nextKey
        }
    }
}