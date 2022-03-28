package com.assignment.githubapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.assignment.githubapp.data.UserRepository
import com.assignment.githubapp.data.remote.RemoteDataSource
import com.assignment.githubapp.model.ApiResult
import com.assignment.githubapp.model.RemoteUserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {

    private lateinit var userRepo: UserRepository

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepo = UserRepository(remoteDataSource, testDispatcher)
    }

    @Test
    fun test_Loading_user_detail() = runBlockingTest {

        val remoteUserDetails = TestData.getRemoteUserDetails()

        Mockito.doReturn(remoteUserDetails)
            .`when`(remoteDataSource)
            .getUserDetails(ArgumentMatchers.anyString())

        val userDetailsFromRepo: ApiResult<RemoteUserDetails> = userRepo.getUserDetails("test")

        var receivedData: RemoteUserDetails? = null

        when(userDetailsFromRepo) {
            is ApiResult.Success -> receivedData = userDetailsFromRepo.data
        }

        assert(userDetailsFromRepo is ApiResult.Success)

        Mockito.verify(remoteDataSource, Mockito.times(1)).getUserDetails("test")

        assert(receivedData?.name.equals(remoteUserDetails.name))
    }


    @Test
    fun test_Loading_user_detail_error() = runBlockingTest {

        val error = RuntimeException("GenericException")

        Mockito.doThrow(error)
            .`when`(remoteDataSource)
            .getUserDetails(ArgumentMatchers.anyString())

        val errorFromRepo: ApiResult<RemoteUserDetails> = userRepo.getUserDetails("test")

        var exception: Throwable? = null

        when(errorFromRepo) {
            is ApiResult.Error -> exception = errorFromRepo.exception
        }

        assert(errorFromRepo is ApiResult.Error)

        Mockito.verify(remoteDataSource, Mockito.times(1)).getUserDetails("test")

        assert(exception?.message.equals(error.message))
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }
}