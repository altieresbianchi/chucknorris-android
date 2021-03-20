package br.com.chucknorris.feature.joke.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.chucknorris.R
import br.com.chucknorris.databinding.ActivityJokeBinding
import br.com.chucknorris.feature.BaseActivity
import br.com.chucknorris.feature.joke.view.adapter.JokeAdapter
import br.com.chucknorris.feature.joke.viewmodel.JokeViewModel
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.global.dialogs.FeedbackBottomSheetDialogFragment
import br.com.chucknorris.repository.model.Joke
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class JokeActivity : BaseActivity() {
    private lateinit var activityJokeBinding: ActivityJokeBinding

    private val viewModel: JokeViewModel by viewModel()

    private val categoryButtonList = mutableListOf<MaterialButton>()
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityJokeBinding = ActivityJokeBinding.inflate(layoutInflater)
        setContentView(activityJokeBinding.root)

        this.prepareUi()
        this.prepareListener()
        this.prepareViewModel()

        viewModel.fetchCategories()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getRandomJoke(selectedCategory)
    }

    private fun prepareUi() {
        try {
            supportActionBar?.title = getString(R.string.joke_screen_title)

            activityJokeBinding.recyclerViewJokes.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@JokeActivity)
                adapter = JokeAdapter(emptyList())
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun prepareListener() {
        activityJokeBinding.apply {
            fabNextJoke.setOnClickListener {
                viewModel.getRandomJoke(selectedCategory)
            }
            recyclerViewJokes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabNextJoke.isExtended()) {
                        fabNextJoke.shrink()
                    } else if (dy < 0 && !fabNextJoke.isExtended()) {
                        fabNextJoke.extend()
                    }
                }
            })
        }
    }

    private fun prepareViewModel() {
        viewModel.apply {
            command.observe(
                this@JokeActivity,
                Observer { cmd ->
                    cmd?.let { triggerCommand(it) }
                }
            )
            commandCategory.observe(
                this@JokeActivity,
                Observer { cmd ->
                    cmd?.let { triggerCommandCategory(it) }
                }
            )
            viewState.observe(
                this@JokeActivity,
                Observer { viewState ->
                    viewState?.let { render(viewState) }
                }
            )
        }
    }

    private fun triggerCommand(command: GenericCommand) {
        when (command) {
            is JokeViewModel.Command.ShowGenericErrorMessage -> {
                showErrorDialogFragment(getString(R.string.error_load_joke))
            }
            is JokeViewModel.Command.ShowJokeCard -> {
                loadJokeCard(command.joke)
            }
            is JokeViewModel.Command.ShowJokeList -> {
                this.loadRecyclerView(command.jokeList)
            }
            is JokeViewModel.Command.ShowEmptyList -> {
                showEmptyList()
            }
        }
    }

    private fun triggerCommandCategory(command: GenericCommand) {
        when (command) {
            is JokeViewModel.Command.ShowCategoryList -> {
                loadCategoryList(command.categoryList)
            }
        }
    }

    private fun render(viewState: JokeViewModel.ViewState) {
        when (viewState.isFetchingJoke || viewState.isFetchingJoke) {
            true -> {
                super.showLoading()
            }
            false -> {
                super.hideLoading()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_joke, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_joke_list -> {
                startActivity(Intent(this, JokeListActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configSearchView(menuItem: MenuItem?) {
        try {
            val searchView = menuItem?.actionView as SearchView
            searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(text: String): Boolean {
                    viewModel.fetchJokesBySearch(text)

                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(s: String): Boolean {
                    activityJokeBinding.recyclerViewJokes.visibility = View.GONE
                    return true
                }
            })
            searchView.setOnCloseListener {
                activityJokeBinding.recyclerViewJokes.visibility = View.VISIBLE
                return@setOnCloseListener false
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun showErrorDialogFragment(message: String) {
        FeedbackBottomSheetDialogFragment.newInstance(
            R.drawable.ic_error,
            getString(R.string.error_fail_title),
            message,
            getString(R.string.btn_ok)
        ).show(supportFragmentManager, FeedbackBottomSheetDialogFragment.TAG)
    }

    private fun loadCategoryList(categoryList: List<String>) {
        activityJokeBinding.apply {
            linearLayoutCategories.removeAllViews()

            for (category in categoryList) {
                val buttonView =
                    LayoutInflater.from(this@JokeActivity).inflate(R.layout.button_category, null)
                val buttonCategory: MaterialButton = buttonView.findViewById(R.id.buttonCategory)
                buttonCategory.text = category
                buttonCategory.tag = category

                buttonCategory.setOnClickListener {
                    if (selectedCategory != null && selectedCategory == it.tag) {
                        selectedCategory = null

                        it.setBackgroundColor(
                            ContextCompat.getColor(this@JokeActivity, R.color.buttonLight)
                        )

                    } else {
                        for (button in categoryButtonList) {
                            button.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@JokeActivity, R.color.buttonLight
                                )
                            )
                        }
                        it.setBackgroundColor(
                            ContextCompat.getColor(this@JokeActivity, R.color.colorPrimary)
                        )
                        selectedCategory = it.tag.toString()
                    }

                    viewModel.getRandomJoke(selectedCategory)
                }

                linearLayoutCategories.addView(buttonView)
                categoryButtonList.add(buttonCategory)
            }
        }
    }

    private fun showCardResult() {
        activityJokeBinding.apply {
            horizontalScrollViewCategories.visibility - View.VISIBLE
            cardViewJoke.visibility - View.VISIBLE
            imageViewIcon.visibility = View.VISIBLE
            textViewJoke.visibility = View.VISIBLE
            recyclerViewJokes.visibility = View.VISIBLE
            fabNextJoke.visibility = View.VISIBLE
        }
    }

    private fun hideCardResult() {
        activityJokeBinding.apply {
            horizontalScrollViewCategories.visibility - View.GONE
            cardViewJoke.visibility = View.GONE
            fabNextJoke.visibility = View.GONE
            recyclerViewJokes.visibility = View.VISIBLE
        }
    }

    private fun loadJokeCard(joke: Joke) {
        activityJokeBinding.apply {
            showCardResult()

            Picasso.get()
                .load(joke.iconUrl)
                .error(R.drawable.ic_error)
                .into(imageViewIcon)

            textViewJoke.text = joke.value

            if (selectedCategory.isNullOrEmpty()) {
                textViewCategory.text = ""
                textViewCategory.visibility = View.GONE
            } else {
                textViewCategory.text = selectedCategory?.toUpperCase(Locale.ROOT)
                textViewCategory.visibility = View.VISIBLE
            }
        }
    }

    private fun loadRecyclerView(list: List<Joke>) {
        activityJokeBinding.apply {
            hideCardResult()

            val adapter = recyclerViewJokes.adapter as JokeAdapter
            adapter.updateList(list)
        }
    }

    private fun showEmptyList() {
        activityJokeBinding.apply {
            imageViewIcon.visibility = View.VISIBLE
            textViewJoke.visibility = View.VISIBLE
            textViewCategory.visibility = View.GONE
            fabNextJoke.visibility = View.GONE

            imageViewIcon.setImageResource(R.drawable.ic_warning)
            textViewJoke.text = getString(R.string.error_empty_list)
        }
    }
}
