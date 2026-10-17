package ru.asmelnikov.rockbluesradio.presentation.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.connectivity.ConnectivityObserver
import ru.asmelnikov.rockbluesradio.testing.MainDispatcherRule

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val connectivity = FakeConnectivityObserver()

    @Test
    fun `connection is assumed until the first update`() {
        val viewModel = MainViewModel(FakeConnectivityObserver(MutableStateFlow(false)))

        assertTrue(viewModel.isConnected.value)
    }

    @Test
    fun `connection state follows the observer`() = runTest {
        val viewModel = MainViewModel(connectivity)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isConnected.collect {} }

        connectivity.isConnected.value = false
        assertFalse(viewModel.isConnected.value)

        connectivity.isConnected.value = true
        assertTrue(viewModel.isConnected.value)
    }

    private class FakeConnectivityObserver(
        override val isConnected: MutableStateFlow<Boolean> = MutableStateFlow(true),
    ) : ConnectivityObserver
}
