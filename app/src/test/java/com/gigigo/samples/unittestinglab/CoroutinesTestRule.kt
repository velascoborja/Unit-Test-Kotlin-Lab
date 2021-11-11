package com.gigigo.samples.unittestinglab

import com.gigigo.samples.unittestinglab.presentation.CoroutinesDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Test rule that changes [Dispatchers.Main] with [TestCoroutineDispatcher]
 */

class CoroutinesTestRule : TestWatcher() {

    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}

class TestCoroutinesDispatchers(
    testCoroutineDispatcher: TestCoroutineDispatcher
) : CoroutinesDispatchers {
    override val Main: CoroutineDispatcher = testCoroutineDispatcher
    override val IO: CoroutineDispatcher = testCoroutineDispatcher
    override val Default: CoroutineDispatcher = testCoroutineDispatcher
    override val Unconfined: CoroutineDispatcher = testCoroutineDispatcher
}
