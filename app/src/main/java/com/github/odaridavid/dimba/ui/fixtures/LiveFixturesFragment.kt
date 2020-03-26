package com.github.odaridavid.dimba.ui.fixtures

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.odaridavid.dimba.R
import com.github.odaridavid.dimba.base.BaseFragment
import com.github.odaridavid.dimba.commons.Error
import com.github.odaridavid.dimba.commons.Success
import com.github.odaridavid.dimba.commons.isVisible
import com.github.odaridavid.dimba.models.fixtures.LiveFixture
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_live_fixtures.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LiveFixturesFragment : BaseFragment<List<LiveFixture>>(R.layout.fragment_live_fixtures) {

    //TODO Display Live Fixture Events
    private val fixturesViewModel: FixturesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        onNetworkChange { isConnected ->
            if (isConnected && fixturesViewModel.fixtures.value is Error)
                fixturesViewModel.getFixtures()
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveFixtures()
    }

    private fun observeLiveFixtures() {
        fixturesViewModel.fixtures.observe(this, Observer { result ->
            handleState(result)
        })
    }

    override fun showLoading(isLoading: Boolean) {
        super.showLoading(isLoading)
        no_live_fixtures_text_view.isVisible(false)
    }

    override fun showOnSuccess(result: Success<List<LiveFixture>>) {
        super.showOnSuccess(result)
        val liveFixtures = result.data
        if (liveFixtures.isEmpty()) showOnEmpty() else setupRecyclerView(liveFixtures)
    }

    private fun showOnEmpty() {
        no_live_fixtures_text_view.isVisible(true)
    }

    private fun setupRecyclerView(liveFixtures: List<LiveFixture>) {
        live_fixtures_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = setupLiveFixtureAdapter(liveFixtures)
        live_fixtures_recycler_view.adapter = ScaleInAnimationAdapter(adapter)
    }

    private fun setupLiveFixtureAdapter(liveFixtures: List<LiveFixture>): LiveFixturesAdapter {
        return LiveFixturesAdapter().apply { submitList(liveFixtures) }
    }

}
