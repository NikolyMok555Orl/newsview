package com.test.newsview.api

import com.google.gson.annotations.SerializedName
import com.test.newsview.data.Article

data class NewsResponse(
    @SerializedName("totalResults") val total: Int = 0,
    @SerializedName("articles") val items: List<Article> = emptyList(),
    val nextPage: Int? = null
)