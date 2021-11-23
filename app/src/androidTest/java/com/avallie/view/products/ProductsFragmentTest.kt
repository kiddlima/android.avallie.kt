package com.avallie.view.products


import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.avallie.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsFragmentTest {

    private lateinit var scenario: FragmentScenario<ProductsFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.AppTheme)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testSearchProduct() {
        onView(withId(R.id.search_user)).perform(typeText("Teste de produto não encontrado"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.no_data_layout)).check(matches(isDisplayed()))
    }

}