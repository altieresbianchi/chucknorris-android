# Chuck Norris - Android
Chuck Norris Facts

# Architecture:
MVVM - Model View ViewModel

![MVVM Architecture](http://www.phaneronsoft.com.br/wp-content/uploads/2021/03/MVVM_Architecture.png?raw=true "Architecture MVVM")

# Applied Techniques
Chuck Norris Facts is an app created for the purpose of presenting knowledge in building Android applications with the Kotlin language. There is no commercial goal in this development.
In this app, some development techniques were used as described below:
- Language: Kotlin with Coroutines
- Architecture: MVVM
- XML Mapping: View Binding
- Dependency Injection: Koin
- HTTP requests: Retrofit
- Unit Tests: JUnit
- Mock for unit tests: Mockito

# API
For this app, the API used was: https://api.chucknorris.io.

*chucknorris.io* is a free JSON API for hand curated Chuck Norris facts.\
Chuck Norris facts are satirical factoids about martial artist and actor Chuck Norris that have become an Internet phenomenon and as a result have become widespread in popular culture. The 'facts' are normally absurd hyperbolic claims about Norris' toughness, attitude, virility, sophistication, and masculinity.

# Functionalities
The app has two screens.

## First Screen: Random Fact
In the first screen, it's possible read a random fact without filter. There is a button to read the next fact.\
On top of this screen, there is a horizontal list of categories. When selecting one of this categories, the next random fact will be displayed and a category marker will be displayed bellow the text and the category selected will be highlighted. To deselect the category, simply touch in this category again.\
If an error occurs, a dialog box with an error message will be displayed.\
On action bar, there is an option menu to open the next screen "Search Facts".

## Second Screen: Search Facts
In the second screen, it is possible make a search by text to show a list of facts.\
There is a search field to type text to filter. When one or more facts are founded, a list of facts will be displayed.\
If no record is found, an empty list message is displayed.\
If an error occurs, a dialog box with an error message will be displayed.
