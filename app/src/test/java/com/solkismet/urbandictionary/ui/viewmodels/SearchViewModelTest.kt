package com.solkismet.urbandictionary.ui.viewmodels

import com.google.gson.Gson
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.network.Service
import com.solkismet.urbandictionary.ui.contracts.SearchEventHandler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Service::class)
@PowerMockIgnore("okhttp3.*")
class SearchViewModelTest {
    @Mock private lateinit var searchView: SearchEventHandler.SearchView
    @Mock private lateinit var service: Service

    private val gson = Gson()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun initialize() {
        PowerMockito.mockStatic(Service::class.java)
        PowerMockito.whenNew(Service::class.java)
            .withAnyArguments()
            .thenReturn(service)
        viewModel = SearchViewModel(searchView, service)
    }

    @Test
    fun `processSearchQuery SHOULD call clearSort WHEN search query is empty`() {
        //GIVEN

        //WHEN
        viewModel.processSearchQuery("")

        //THEN
        verify(searchView).clearSort()
    }

    @Test
    fun `processSearchQuery SHOULD call setSearchResult WHEN search query is empty`() {
        //GIVEN

        //WHEN
        viewModel.processSearchQuery("")

        //THEN
        verify(searchView).setSearchResult(any())
    }

    @Test
    fun `processSearchQuery SHOULD call clearSort WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        `when`(service.search(anyString())).thenAnswer {
            return@thenAnswer Single.just(successfulResponse)
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(searchView).clearSort()
        verify(searchView).setIsRefreshing(true)
    }

    @Test
    fun `processSearchQuery SHOULD call setIsRefreshing with true WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        `when`(service.search(anyString())).thenAnswer {
            return@thenAnswer Single.just(successfulResponse)
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(searchView).setIsRefreshing(true)
    }

    @Test
    fun `processSearchQuery SHOULD call setSearchResult WHEN search query is not empty AND search api returns a valid response`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        `when`(service.search(anyString())).thenAnswer {
            return@thenAnswer Single.just(successfulResponse)
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        verify(searchView).setSearchResult(successfulResponse)
    }

    @Test
    fun `processSearchQuery SHOULD call setIsRefreshing with false WHEN search query is not empty`() {
        //GIVEN
        val searchTerm = "sweet"
        val successfulResponse = createSuccessfulSearchResponse()
        `when`(service.search(anyString())).thenAnswer {
            return@thenAnswer Single.just(successfulResponse)
        }

        //WHEN
        viewModel.processSearchQuery(searchTerm)

        //THEN
        verify(searchView).setIsRefreshing(false)
    }

    @Test
    fun `processSearchQuery SHOULD call setSearchResult WHEN search query is not empty AND search api returns an error`() {
        //GIVEN
        val throwable = mock(Throwable::class.java)
        `when`(service.search(anyString())).thenAnswer {
            Single.error<SearchResult>(throwable)
        }

        //WHEN
        viewModel.processSearchQuery("sweet")

        //THEN
        verify(searchView).showError()
    }

    @Test
    fun `refreshSearch SHOULD call searchTerm WHEN current search term is not empty`() {
        //GIVEN
        val successfulResponse = createSuccessfulSearchResponse()
        `when`(service.search(anyString())).thenAnswer {
            return@thenAnswer Single.just(successfulResponse)
        }

        //WHEN
        viewModel.processSearchQuery("sweet")
        viewModel.refreshSearch()

        //THEN
        verify(searchView, times(2)).clearSort()
        verify(searchView, times(2)).setIsRefreshing(true)
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
