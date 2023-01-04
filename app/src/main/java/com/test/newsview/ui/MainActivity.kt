package com.test.newsview.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.savedstate.SavedStateRegistryOwner
import com.test.newsview.R
import com.test.newsview.data.Article
import com.test.newsview.data.ArticleRepository
import com.test.newsview.databinding.ActivityMainBinding
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.test.newsview.data.Source
import com.test.newsview.theme.AppTheme
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.test.newsview.data.ArticleRepository.Companion.categories
import com.test.newsview.data.ArticleRepository.Companion.countries



class MainActivity : AppCompatActivity() {


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(owner = this)
        )[ArticleViewModel::class.java]

    }

    override fun onStart() {
        super.onStart()
        // get the view model
        binding.composeView.setContent {
            AppTheme {
                ArticleScreen()
            }
        }

    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun ArticleScreen() {
        val userListItems: LazyPagingItems<Article> = viewModel.searchedNews.collectAsLazyPagingItems()
        val openDialog = remember { mutableStateOf(false) }
        Surface(
            color = MaterialTheme.colors.background
        ) {
            Scaffold(
                topBar = { SearchAppBar()
                }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(contentPadding = PaddingValues(4.dp)) {
                        items(userListItems) { item ->
                            item?.let {
                                CardArticle(article = it, openArticle = {
                                    if(!it.url.isNullOrEmpty()) {
                                        openWeb(it.url)
                                    }else{
                                        openDialog.value=true
                                    }
                                })
                            }
                        }
                        userListItems.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                                CircularProgressIndicator()
                                            }
                                    }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e = userListItems.loadState.refresh as LoadState.Error
                                    item{
                                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                                           Text(text=e.toString())
                                        }

                                    }
                                }
                                loadState.append is LoadState.Loading -> {
                                   item {
                                       Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                           CircularProgressIndicator()
                                       }
                                   }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e = userListItems.loadState.append as LoadState.Error
                                    item{
                                        Row(horizontalArrangement = Arrangement.Center,  modifier = Modifier.fillMaxWidth()) {
                                            Text(text=e.toString())
                                        }
                                    }

                                }
                            }
                        }
                    }
                    if (openDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog.value = false
                            },
                            title = { Text(text = "Ссылка на новость не действительна") },
                            buttons = {
                                Button(
                                    onClick = { openDialog.value = false }
                                ) {
                                    Text("OK", fontSize = 22.sp)
                                }
                            }
                        )
                    }
                }
            }

        }

    }


    @Composable
    fun CardArticle(article: Article, openArticle: () -> Unit, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 2.dp)
                .clickable(onClick = { openArticle() }),
            elevation = 6.dp
        ) {
            Column(modifier = modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            if (!article.urlToImage.isNullOrEmpty()) article.urlToImage else
                                resources.getDrawable(R.drawable.ic_no_photography_80, null)
                        )
                        .crossfade(true)
                        .build(), contentDescription = "Фото", contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_baseline_downloading_80),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)

                        .clip(RoundedCornerShape(2.dp))
                )
                Column(modifier=Modifier.padding(4.dp)) {
                    Text(
                        text = article.title, style = MaterialTheme.typography.h6.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ), maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = article.description ?: "",
                        style = MaterialTheme.typography.body1,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    /*  if (!article.author.isNullOrEmpty()) {
                        Text(
                            text = "Автор: ${article.author}",
                            style = MaterialTheme.typography.body2
                        )
                    }*/
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = "${article.source.name}",
                            style = MaterialTheme.typography.overline.copy(fontStyle = FontStyle.Italic),
                            modifier=Modifier.weight(1f)
                        )
                        Text(
                            text = article.publishedAt, style = MaterialTheme.typography.overline,
                            textAlign = TextAlign.End,  modifier=Modifier.weight(1f)
                        )

                    }
                }
            }
        }
    }


    @Composable
    private fun SearchAppBar() {
        var showClearIcon by rememberSaveable { mutableStateOf(false) }
        val uiState=viewModel.state.observeAsState()
        var expandedLanguage by remember { mutableStateOf(false) }
        var expandedCategory by remember { mutableStateOf(false) }
        fun searchNews(){
            expandedLanguage=false
            expandedCategory=false
            viewModel.searchNews()
        }
        SearchAppBar(
            { onQueryChanged: String -> uiState.value?.query = onQueryChanged },
            showClearIcon, { changeValue: Boolean -> showClearIcon = changeValue }, { searchNews() },
            expandedLanguage,
            { country: ArticleRepository.Country -> uiState.value?.country=country
                searchNews() },
            countries, { expandedLanguage = !expandedLanguage },
            expandedCategory,
            { category: ArticleRepository.Category ->
                uiState.value?.category=category
                searchNews()
            },
            categories, { expandedCategory = !expandedCategory }, uiState.value?:UiState()
        )
    }


    @Composable
    private fun SearchAppBar(
        onQueryChange: (onQueryChanged: String) -> Unit, showClearIcon: Boolean,
        showClearIconChange: (changeValue: Boolean) -> Unit, search:()->Unit, expandedLanguage:Boolean,
        selectedOptionLanguage:(country:ArticleRepository.Country)->Unit, countries: List<ArticleRepository.Country>,
        openMenuCountry:()->Unit, expandedCategory:Boolean,
        selectedOptionCategory:(category:ArticleRepository.Category)->Unit, category: List<ArticleRepository.Category>,
        openMenuCategory:()->Unit, uiState: UiState

    ) {
        Column {
            if (uiState.query.isEmpty()) {
                showClearIconChange(false)
            } else if (uiState.query.isNotEmpty()) {
                showClearIconChange(true)
            }
            TextField(
                value = uiState.query,
                onValueChange = { onQueryChanged ->
                    onQueryChange(onQueryChanged)
                },
                leadingIcon = {
                    IconButton(onClick = { search() }) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            tint = MaterialTheme.colors.onBackground,
                            contentDescription = "Search Icon"
                        )
                    }
                },
                trailingIcon = {
                    if (showClearIcon) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                tint = MaterialTheme.colors.onBackground,
                                contentDescription = "Clear Icon"
                            )
                        }
                    }
                },
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                placeholder = { Text(text = stringResource(R.string.hint_search_query)) },
                textStyle = MaterialTheme.typography.subtitle1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.background, shape = RectangleShape)
            )

            Row(modifier = Modifier.fillMaxWidth()){
                TextButton(onClick = { openMenuCategory() }, modifier = Modifier.weight(1f)) {
                    Text(uiState.category.title)
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "")
                    if(expandedCategory){
                        DropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { openMenuCategory() },
                        ) {
                            category.forEach {
                                DropdownMenuItem(onClick = {selectedOptionCategory(it) }) {
                                    Text(it.title)
                                }
                            }
                        }

                    }
                }

                TextButton(onClick = { openMenuCountry() }, modifier = Modifier.weight(1f)) {
                    Text(uiState.country.title)
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "")
                    if(expandedLanguage){
                        DropdownMenu(
                            expanded = expandedLanguage,
                            onDismissRequest = { openMenuCountry() },

                        ) {
                            countries.forEach {
                                DropdownMenuItem(onClick = {selectedOptionLanguage(it) }) {
                                    Text(it.title)
                                }
                            }
                        }
                    }
                }


            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PeviewCardArticle() {

        AppTheme {
            val article = Article(
                source = Source("google-news", "Google News"),
                title = "Kia в России объявила цены на новый седан К9 - Авто Mail.ru",
                description = "На российском официальном сайте Kia появился прайс-лист флагманского седана K9. В России автомобиль доступен в единственной комплектации Premium",
                url = "",
                urlToImage = "",
                publishedAt = "2022-09-03T08:29:00Z",
                content = null,
                author = "Авто Mail.ru"
            )
            CardArticle(article, {}, Modifier)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PeviewSearchAppBar() {
        AppTheme {
           /* SearchAppBar("", {}, false, {}, {}, false, DEFAULT_COUNTRY, {  }, listOf(), {},
                false, DEFAULT_CATEGORY, {  }, listOf(), {})*/
        }
    }


    fun openWeb(url:String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


}


object Injection {

    /**
     * Creates an instance of [ArticleRepository]
     */
    private fun provideArticleRepository(): ArticleRepository = ArticleRepository()

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideArticleRepository())
    }
}