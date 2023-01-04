package com.test.newsview.data

import java.sql.Time
import java.util.*
import kotlin.random.Random


data  class Article(
    /**Источник*/
    val source:Source,
    val author: String?,
    val title:String,
    val description:String?,
    val url:String?,
    val urlToImage: String?,
    val publishedAt:String,
    val content: String?,
    //Для пагинации
    var id:Int= Random(Calendar.getInstance().time.time.toInt()).nextInt()
)


data class Source( val id: String?, val name:String)

