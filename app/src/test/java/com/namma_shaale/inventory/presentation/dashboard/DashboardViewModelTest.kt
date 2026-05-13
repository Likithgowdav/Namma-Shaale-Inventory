package com.namma_shaale.inventory.presentation.dashboard

import com.namma_shaale.inventory.data.repository.AssetRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After

class DashboardViewModelTest {
    private lateinit var viewModel: DashboardViewModel
    private val mockRepository = mockk<AssetRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = DashboardViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadDashboardData() = runTest {
        coEvery { mockRepository.getTotalAssetCount() } returns 50
        coEvery { mockRepository.getWorkingAssetCount() } returns 45
        coEvery { mockRepository.getNeedsRepairCount() } returns 3
        coEvery { mockRepository.getBrokenLostCount() } returns 2
        coEvery { mockRepository.getPendingRepairCount() } returns 1

        viewModel.loadDashboardData()

        assertEquals(50, viewModel.uiState.value.totalAssets)
        assertEquals(45, viewModel.uiState.value.greenCount)
        assertEquals(3, viewModel.uiState.value.yellowCount)
        assertEquals(2, viewModel.uiState.value.redCount)
        assertEquals(1, viewModel.uiState.value.pendingRepairs)
    }
}
