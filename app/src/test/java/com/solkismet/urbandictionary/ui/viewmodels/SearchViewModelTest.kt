package com.solkismet.urbandictionary.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.solkismet.urbandictionary.data.di.networkModule
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.network.SearchService
import io.reactivex.Flowable
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
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
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest : KoinTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()
    @Mock private lateinit var onSearchAction: SearchViewModel.OnSearchAction
    private lateinit var viewModel: SearchViewModel
    private val gson = Gson()

    @Before
    fun initialize() {
        startKoin { modules(networkModule) }
        onSearchAction = mock(SearchViewModel.OnSearchAction::class.java)
        viewModel = SearchViewModel(onSearchAction)
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
        verify(onSearchAction).clearSort()
    }

    @Test
    fun `processSearchQuery SHOULD return null result WHEN search query is empty`() {
        //GIVEN

        //WHEN
        viewModel.processSearchQuery("")

        //THEN
        assertEquals(viewModel.searchResult.value, null)
    }

    @Test
    fun `processSearchQuery SHOULD call clearSort WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(onSearchAction).clearSort()
        verify(onSearchAction).setIsRefreshing(true)
    }

    @Test
    fun `processSearchQuery SHOULD call setIsRefreshing with true WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(onSearchAction).setIsRefreshing(true)
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
        assertNotNull(viewModel.searchResult.value)
        assertNotNull(viewModel.searchResult.value?.list)
        assertEquals(viewModel.searchResult.value?.list?.size, 1)
        assertNotNull(viewModel.searchResult.value?.list?.get(0))
        assertEquals(viewModel.searchResult.value?.list?.get(0)?.word, "Sweet")
    }

    @Test
    fun `processSearchQuery SHOULD call setIsRefreshing with false WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(onSearchAction).setIsRefreshing(false)
    }

    @Test
    fun `processSearchQuery SHOULD call setSearchResult WHEN search query is not empty AND search api returns an error`() {
        //GIVEN
        val throwable = mock(Throwable::class.java)
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.error(throwable))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        verify(onSearchAction).showError()
    }

    @Test
    fun `refreshSearch SHOULD call searchTerm WHEN current search term is not empty`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        declareMock<SearchService> {
            given(this.search(anyString())).willReturn(Flowable.just(successfulResponse))
        }

        //WHEN
        viewModel.processSearchQuery("sweet")
        viewModel.refreshSearch()

        //THEN
        verify(onSearchAction, times(2)).clearSort()
        verify(onSearchAction, times(2)).setIsRefreshing(true)
    }

    private fun createSuccessfulSearchResponse(): SearchResult {
        return gson.fromJson(SAMPLE_RESPONSE_JSON, SearchResult::class.java)
    }

    private companion object {
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
    }
}