package br.com.chucknorris.feature.joke.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.chucknorris.R
import br.com.chucknorris.databinding.ActivityJokeListBinding
import br.com.chucknorris.feature.BaseActivity
import br.com.chucknorris.feature.joke.view.adapter.JokeAdapter
import br.com.chucknorris.feature.joke.viewmodel.JokeListViewModel
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.global.dialogs.FeedbackBottomSheetDialogFragment
import br.com.chucknorris.repository.model.Joke
import com.mancj.materialsearchbar.MaterialSearchBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class JokeListActivity : BaseActivity() {
    private lateinit var activityJokeListBinding: ActivityJokeListBinding

    private val viewModel: JokeListViewModel by viewModel()
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityJokeListBinding = ActivityJokeListBinding.inflate(layoutInflater)
        setContentView(activityJokeListBinding.root)

        this.prepareUi()
        this.prepareListener()
        this.prepareViewModel()
    }

    private fun prepareUi() {
        try {
            supportActionBar?.title = getString(R.string.joke_list_screen_title)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            activityJokeListBinding.apply {
                textViewInfo.visibility = View.VISIBLE

                recyclerViewJokes.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@JokeListActivity)
                    adapter = JokeAdapter(emptyList())
                }
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun prepareListener() {
        activityJokeListBinding.apply {
            searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
                override fun onSearchStateChanged(enabled: Boolean) {
                }

                override fun onSearchConfirmed(text: CharSequence) {
                    if (text.isNotEmpty()) {
                        searchText = text.toString()
                        viewModel.fetchJokesBySearch(searchText)

                        loadSearchText()
                    }
                }

                override fun onButtonClicked(buttonCode: Int) {
                }
            })
        }
    }

    private fun prepareViewModel() {
        viewModel.apply {
            command.observe(
                this@JokeListActivity,
                Observer { cmd ->
                    cmd?.let { triggerCommand(it) }
                }
            )
            viewState.observe(
                this@JokeListActivity,
                Observer { viewState ->
                    viewState?.let { render(viewState) }
                }
            )
        }
    }

    private fun triggerCommand(command: GenericCommand) {
        when (command) {
            is JokeListViewModel.Command.ShowGenericErrorMessage -> {
                showErrorDialogFragment(getString(R.string.error_search_joke))
            }
            is JokeListViewModel.Command.ShowJokeList -> {
                loadRecyclerView(command.jokeList)
            }
            is JokeListViewModel.Command.ShowEmptyList -> {
                showEmptyList()
            }
        }
    }

    private fun render(viewState: JokeListViewModel.ViewState) {
        when (viewState.isFetchingJoke) {
            true -> {
                super.showLoading()
            }
            false -> {
                super.hideLoading()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showErrorDialogFragment(message: String) {
        FeedbackBottomSheetDialogFragment.newInstance(
            R.drawable.ic_error,
            getString(R.string.error_fail_title),
            message,
            getString(R.string.btn_ok)
        ).show(supportFragmentManager, FeedbackBottomSheetDialogFragment.TAG)
    }

    private fun loadSearchText() {
        activityJokeListBinding.apply {
            textViewInfo.visibility = View.GONE
            textViewSearchText.visibility = View.VISIBLE

            textViewSearchText.text = getString(R.string.joke_list_search_result, searchText)
        }
    }

    private fun loadRecyclerView(list: List<Joke>) {
        activityJokeListBinding.apply {
            textViewInfo.visibility = View.GONE
            linearLayoutEmptyList.visibility = View.GONE
            recyclerViewJokes.visibility = View.VISIBLE

            val adapter = recyclerViewJokes.adapter as JokeAdapter
            adapter.updateList(list)
        }
    }

    private fun showEmptyList() {
        activityJokeListBinding.apply {
            textViewInfo.visibility = View.GONE
            linearLayoutEmptyList.visibility = View.VISIBLE
            recyclerViewJokes.visibility = View.GONE
        }
    }
}
