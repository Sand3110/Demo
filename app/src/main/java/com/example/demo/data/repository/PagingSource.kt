package com.example.demo.data.repository

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.demo.data.api.ApiService
import com.example.demo.data.data.model.CharacterData

class PagingSource(private val apiService: ApiService) : PagingSource<Int, CharacterData>() {
    override fun getRefreshKey(state: PagingState<Int, CharacterData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterData> {
        return try {
            val nextPage: Int = params.key ?: FIRST_PAGE_INDEX
            val response = apiService.getDataFromAPI(nextPage).body()

            var nextPageNumber: Int? = null
            if (response?.info?.next != null) {
                val uri = Uri.parse(response.info.next)
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageNumber = nextPageQuery?.toInt()
            }
            var prevPageNumber: Int? = null
            if (response?.info?.prev != null) {
                val uri = Uri.parse(response.info.prev)
                val prevPageQuery = uri.getQueryParameter("page")

                prevPageNumber = prevPageQuery?.toInt()
            }

            LoadResult.Page(data = response?.results!!,
                prevKey = prevPageNumber,
                nextKey = nextPageNumber)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}