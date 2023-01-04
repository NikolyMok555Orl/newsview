package com.test.newsview.ui

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.test.newsview.data.Article
import com.test.newsview.data.ArticleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

class ArticleViewModel(
    private val repository: ArticleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Состояние.
     */
    val state: MutableLiveData<UiState> by lazy { MutableLiveData<UiState>(UiState()) }


    private val _searchedNews = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val searchedNews = _searchedNews

    /* private val _query: MutableLiveData<String>  by lazy { MutableLiveData<String>(DEFAULT_QUERY) }

        val query: LiveData<String>
        get() = _query


    private val _country: MutableLiveData<ArticleRepository.Country>
    by lazy { MutableLiveData<ArticleRepository.Country>(DEFAULT_COUNTRY) }
    /**Страна или язык новостей*/
    val country: LiveData<ArticleRepository.Country>
        get() = _country


    private val _category: MutableLiveData<ArticleRepository.Category>
    by lazy { MutableLiveData<ArticleRepository.Category>(DEFAULT_CATEGORY) }
    val category: LiveData<ArticleRepository.Category>
        get() = _category*/

    init {
        searchNews()
    }



    /*fun setQuery(newQuery: String){
        _query.value=newQuery
    }

    fun setCountry(newCountry:  ArticleRepository.Country){
        _country.value=newCountry
    }

    fun setCategory(newCategory: ArticleRepository.Category){
        _category.value=newCategory
    }*/

     fun searchNews() {
         viewModelScope.launch {
             repository.getSearchResultStream(query = state.value?.query, country = state.value?.country?: DEFAULT_COUNTRY, category=state.value?.category?: DEFAULT_CATEGORY).
             cachedIn(viewModelScope).collect {
                 _searchedNews.value = it
             }
         }
    }


}
data class UiState(
    var query:String=DEFAULT_QUERY,
    var country:ArticleRepository.Country=DEFAULT_COUNTRY,
    var category:ArticleRepository.Category=DEFAULT_CATEGORY
)


private const val DEFAULT_QUERY =""
val DEFAULT_COUNTRY = ArticleRepository.countries.first()
val DEFAULT_CATEGORY = ArticleRepository.categories.first()