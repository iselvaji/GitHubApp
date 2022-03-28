package com.assignment.githubapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.assignment.githubapp.data.remote.RemoteDataSource
import com.assignment.githubapp.data.remote.UserPagingSource
import com.assignment.githubapp.model.RemoteUser
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserPagingSourceTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var userDataSource: RemoteDataSource

    private lateinit var userPagingSource: UserPagingSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userPagingSource = UserPagingSource(userDataSource, "")
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun test_user_paging_source_load_failure() = runBlockingTest {

        val error = HttpException(
            Response.error<ResponseBody>(403
            , "some content".toResponseBody("plain/text".toMediaTypeOrNull())
        ))

        Mockito.doThrow(error)
            .`when`(userDataSource)
            .searchUser(anyString(), anyInt(), anyInt())

        val expectedResult = PagingSource.LoadResult.Error<Int, RemoteUser>(error)

        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun test_user_paging_source_load_refresh_success() = runBlockingTest {

        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .searchUser(anyString(),anyInt(),anyInt())

        val expectedResult = PagingSource.LoadResult.Page(
            data = users.items,
            prevKey = 0,
            nextKey = 2
        )

        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 30,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun test_user_paging_source_load_prepend_success()= runBlockingTest {

        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .searchUser(anyString(),anyInt(),anyInt())

        val expectedResult = PagingSource.LoadResult.Page(
            data = users.items,
            prevKey = 0,
            nextKey = 2
        )

        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 1,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun test_user_paging_source_load_append_success() = runBlockingTest {

        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .searchUser(anyString(),anyInt(),anyInt())

        val expectedResult = PagingSource.LoadResult.Page(
            data = users.items,
            prevKey = 1,
            nextKey = null
        )

        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 2,
                    loadSize = 30,
                    placeholdersEnabled = false
                )
            )
        )
    }
}