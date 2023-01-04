package com.test.newsview.data

import androidx.paging.*
import com.test.newsview.api.Api
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException



class ArticleRepository() {
    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String?=null, country:Country, category:Category): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { ArticlePagingSource(query, country.id, category.id) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10

            val countries:List<Country> = listOf(Country(null, "Все страны"),Country("ru", "Россия"),
                Country("us", "США"), Country("de", "Германия"), Country("cn", "КНР"))


            val categories:List<Category> = listOf(Category("general", "Главные"), Category("business", "Бизнес"),
                Category("entertainment", "Развлечение"),  Category("science", "Наука"),
                Category("sports", "Спорт"), Category("technology", "Технологии"))

    }



    data class Country(val id:String?, val title:String)

    data class Category(val id:String?, val title:String)

}