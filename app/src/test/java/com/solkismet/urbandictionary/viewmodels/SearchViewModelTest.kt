package com.solkismet.urbandictionary.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.solkismet.urbandictionary.data.db.WordDetailDao
import com.solkismet.urbandictionary.data.di.dataBaseModule
import com.solkismet.urbandictionary.data.di.networkModule
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.network.SearchService
import io.reactivex.Flowable
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest : KoinTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: SearchViewModel
    private val gson = Gson()

    @Before
    fun initialize() {
        startKoin { modules(networkModule, dataBaseModule) }
        viewModel = SearchViewModel()
    }

    @After
    fun shutdown() {
        stopKoin()
    }

    @Test
    fun `processSearchQuery SHOULD call clearSort WHEN search query is empty`() {
        //GIVEN

        //WHEN
        viewModel.processSearchQuery("")

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.CLEAR_SORT,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `processSearchQuery SHOULD return null result WHEN search query is empty`() {
        //GIVEN

        //WHEN
        viewModel.processSearchQuery("")

        //THEN
        assertEquals(viewModel.getSearchResult().value, null)
    }

    @Test
    fun `processSearchQuery SHOULD call clear sort AND set is refreshing WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        var firstActionResult: SearchViewModel.OnSearchAction? = null
        var secondActionResult: SearchViewModel.OnSearchAction? = null
        viewModel.getSearchActionEvent().getOrAwaitValueWithCallback(
            mutableListOf<(value: SearchViewModel.OnSearchAction?) -> Unit>().apply {
                add { firstActionResult = it }
                add { secondActionResult = it }
            }
        )

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.CLEAR_SORT,
            firstActionResult
        )
        assertEquals(
            SearchViewModel.OnSearchAction.SET_IS_REFRESHING,
            secondActionResult
        )
    }

    @Test
    fun `processSearchQuery SHOULD return 1 element WHEN search query is not empty AND search api returns a valid response`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        assertNotNull(viewModel.getSearchResult().value)
        assertNotNull(viewModel.getSearchResult().value?.list)
        assertEquals(1, viewModel.getSearchResult().value?.list?.size)
        assertNotNull(viewModel.getSearchResult().value?.list?.get(0))
        assertEquals("Sweet", viewModel.getSearchResult().value?.list?.get(0)?.word)
    }

    @Test
    fun `processSearchQuery SHOULD call set is not refreshing WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.SET_IS_NOT_REFRESHING,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `processSearchQuery SHOULD call show error WHEN search query is not empty AND search api returns an error`() {
        //GIVEN
        val throwable = mock(Throwable::class.java)
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.error(throwable))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.SHOW_ERROR,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `refreshSearch SHOULD call clear sort AND set is refreshing WHEN current search term is not empty`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        var firstActionResult: SearchViewModel.OnSearchAction? = null
        var secondActionResult: SearchViewModel.OnSearchAction? = null
        viewModel.getSearchActionEvent().getOrAwaitValueWithCallback(
            mutableListOf<(value: SearchViewModel.OnSearchAction?) -> Unit>().apply {
                add { firstActionResult = it }
                add { secondActionResult = it }
            }
        )

        //WHEN
        viewModel.processSearchQuery("sweet")
        viewModel.refreshSearch()

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.CLEAR_SORT,
            firstActionResult
        )
        assertEquals(
            SearchViewModel.OnSearchAction.SET_IS_REFRESHING,
            secondActionResult
        )
    }

    @Test
    fun `handleSearchResults SHOULD call hideEmptySearchResults WHEN current search term is not empty`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")
        viewModel.handleSearchResults()

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.HIDE_EMPTY_SEARCH_RESULTS,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `handleSearchResults SHOULD call hideEmptySearchResults WHEN current search term is empty`() {
        //GIVEN
        val emptySuccessfulResponse = createEmptySuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(emptySuccessfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")
        viewModel.handleSearchResults()

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.SHOW_EMPTY_SEARCH_RESULTS,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `handleSearchResults SHOULD call showStartSearch WHEN there is no current search`() {
        //GIVEN

        //WHEN
        viewModel.handleSearchResults()

        //THEN
        assertEquals(
            SearchViewModel.OnSearchAction.SHOW_START_SEARCH,
            viewModel.getSearchActionEvent().getOrAwaitValue()
        )
    }

    @Test
    fun `processSearchQuery SHOULD return 1 element WHEN search query is not empty AND worddetail dao returns a valid response AND network is turned off AND valid data is in the database`() {
        //GIVEN
        viewModel.online = false
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<WordDetailDao> {
            given(this.searchForWord(anyString())).willReturn(Flowable.just(successfulResponse.list))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        assertNotNull(viewModel.getSearchResult().value)
        assertNotNull(viewModel.getSearchResult().value?.list)
        assertEquals(1, viewModel.getSearchResult().value?.list?.size)
        assertNotNull(viewModel.getSearchResult().value?.list?.get(0))
        assertEquals("Sweet", viewModel.getSearchResult().value?.list?.get(0)?.word)
    }

    @Test
    fun `processSearchQuery SHOULD not return any elements WHEN search query is not empty AND search api returns a valid response AND network is turned of AND valid data is not in the database`() {
        //GIVEN
        viewModel.online = false
        declareMock<WordDetailDao> {
            given(this.searchForWord(anyString())).willReturn(Flowable.just(mutableListOf()))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        assertNotNull(viewModel.getSearchResult().value)
        assertNotNull(viewModel.getSearchResult().value?.list)
        assertEquals(0, viewModel.getSearchResult().value?.list?.size)
    }

    @Test
    fun `sortResultsByThumbsUp SHOULD return a list sorted by thumbs up in descending order`() {
        // GIVEN
        val successfulResponse = createSuccessfulMulipleSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }
        viewModel.processSearchQuery("sweet")

        // WHEN
        val sortedList = viewModel.sortResultsByThumbsUp()?.list

        // THEN
        assertNotNull(sortedList)
        assertEquals(3, sortedList?.size)
        assertNotNull(sortedList?.get(0))
        assertEquals(200, sortedList?.get(0)?.thumbsUp)
        assertNotNull(sortedList?.get(1))
        assertEquals(81, sortedList?.get(1)?.thumbsUp)
        assertNotNull(sortedList?.get(2))
        assertEquals(5, sortedList?.get(2)?.thumbsUp)
    }

    @Test
    fun `sortResultsByThumbsDown SHOULD return a list sorted by thumbs down in descending order`() {
        // GIVEN
        val successfulResponse = createSuccessfulMulipleSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }
        viewModel.processSearchQuery("sweet")

        // WHEN
        val sortedList = viewModel.sortResultsByThumbsDown()?.list

        // THEN
        assertNotNull(sortedList)
        assertEquals(3, sortedList?.size)
        assertNotNull(sortedList?.get(0))
        assertEquals(25, sortedList?.get(0)?.thumbsDown)
        assertNotNull(sortedList?.get(1))
        assertEquals(19, sortedList?.get(1)?.thumbsDown)
        assertNotNull(sortedList?.get(2))
        assertEquals(18, sortedList?.get(2)?.thumbsDown)
    }

    private fun createSuccessfulSearchResponse(): SearchResult {
        return gson.fromJson(SAMPLE_RESPONSE_JSON, SearchResult::class.java)
    }

    private fun createEmptySuccessfulSearchResponse(): SearchResult {
        return gson.fromJson(EMPTY_SAMPLE_RESONSE_JSON, SearchResult::class.java)
    }

    private fun createSuccessfulMulipleSearchResponse(): SearchResult {
        return gson.fromJson(SAMPLE_MULTIPLE_RESPONSE_JSON, SearchResult::class.java)
    }

    private companion object {
        const val EMPTY_SAMPLE_RESONSE_JSON = "{\n" +
                "    \"list\": [\n" +
                "   ]\n" +
                "}"

        const val SAMPLE_RESPONSE_JSON = "{\n" +
                "    \"list\": [\n" +
                "        {\n" +
                "            \"definition\": \"[Cute]+[nice]\",\n" +
                "            \"permalink\": \"http://sweet.urbanup.com/8413115\",\n" +
                "            \"thumbs_up\": 81,\n" +
                "            \"sound_urls\": [],\n"+
                "            \"author\": \"Joostice\",\n" +
                "            \"word\": \"Sweet\",\n" +
                "            \"defid\": 8413115,\n" +
                "            \"current_vote\": \"\",\n" +
                "            \"written_on\": \"2015-10-06T00:00:00.000Z\",\n" +
                "            \"example\": \"\\\"He's so sweet because he says [nice things] that have [a cute] [effect]\\\"\",\n" +
                "            \"thumbs_down\": 18\n" +
                "        }\n" +
                "   ]\n" +
                "}"

        const val SAMPLE_MULTIPLE_RESPONSE_JSON = "{\n" +
                "    \"list\": [\n" +
                "        {\n" +
                "            \"definition\": \"[Cute]+[nice]\",\n" +
                "            \"permalink\": \"http://sweet.urbanup.com/8413115\",\n" +
                "            \"thumbs_up\": 81,\n" +
                "            \"sound_urls\": [],\n"+
                "            \"author\": \"Joostice\",\n" +
                "            \"word\": \"Sweet\",\n" +
                "            \"defid\": 8413115,\n" +
                "            \"current_vote\": \"\",\n" +
                "            \"written_on\": \"2015-10-06T00:00:00.000Z\",\n" +
                "            \"example\": \"\\\"He's so sweet because he says [nice things] that have [a cute] [effect]\\\"\",\n" +
                "            \"thumbs_down\": 18\n" +
                "        },\n" +
                "        {\n" +
                "            \"definition\": \"[Cute]+[nice]\",\n" +
                "            \"permalink\": \"http://sweet.urbanup.com/8413115\",\n" +
                "            \"thumbs_up\": 200,\n" +
                "            \"sound_urls\": [],\n"+
                "            \"author\": \"Joostice3\",\n" +
                "            \"word\": \"Sweet\",\n" +
                "            \"defid\": 8413200,\n" +
                "            \"current_vote\": \"\",\n" +
                "            \"written_on\": \"2015-10-06T00:00:00.000Z\",\n" +
                "            \"example\": \"\\\"He's so sweet because he says [nice things] that have [a cute] [effect]\\\"\",\n" +
                "            \"thumbs_down\": 19\n" +
                "        },\n" +
                "        {\n" +
                "            \"definition\": \"[Cute]+[nice]\",\n" +
                "            \"permalink\": \"http://sweet.urbanup.com/8413115\",\n" +
                "            \"thumbs_up\": 5,\n" +
                "            \"sound_urls\": [],\n"+
                "            \"author\": \"Joostice2\",\n" +
                "            \"word\": \"Sweet\",\n" +
                "            \"defid\": 8413119,\n" +
                "            \"current_vote\": \"\",\n" +
                "            \"written_on\": \"2015-10-06T00:00:00.000Z\",\n" +
                "            \"example\": \"\\\"He's so sweet because he says [nice things] that have [a cute] [effect]\\\"\",\n" +
                "            \"thumbs_down\": 25\n" +
                "        }\n" +
                "   ]\n" +
                "}"
    }
}
