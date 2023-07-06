<p align="center">
<img src="previews/branding_image_pokepedia.png" width="320"/>
</p>

## Description
**Pokepedia** is an app that serves as your ultimate Pokemon reference guide. With a complete Pokedex, you can quickly find any Pokemon and learn all about their stats, moves, and abilities. 
Filter by type, generation, or ability to easily find the Pokemon you need. You can also create your own custom team and add Pokemon to your favourites list for easy access.<br> 
It demonstrates modern Android development with Hilt, Coroutines, Flow, Jetpack (Room, ViewModel), and Material Design based on MVVM architecture.

![description](previews/screenshot.png)

<img src="previews/preview.gif" align="right" width="320"/>

## Tech stack and Open-source libraries
- Minimum SDK level 21
- Built in [Kotlin](https://kotlinlang.org/) and utilizes [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [Flows](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for handling asynchronous operations.
- Jetpack components:
    - Lifecycle: Observe Android lifecycles and manages UI states based on lifecycle changes.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): Manages UI-related data and is lifecycle-aware, ensuring data survives configuration changes like screen rotations.
    - [Room](https://developer.android.com/training/data-storage/room/): Provides an abstraction layer over SQLite, facilitating smooth database access.
    - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore): Store key-value pairs or typed objects asynchronously, consistently, and transactionally.
    - [Hilt](https://dagger.dev/hilt/): Used for dependency injection.
    - [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview): Helps you load and display pages of data from a larger dataset from local storage or over network.
- Architecture
    - MVVM Architecture (View - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Model)
    - Repository Pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): Construct the REST APIs and paging network data.
- [Glide](https://github.com/bumptech/glide): Loading images from network.
- [Palette](https://developer.android.com/reference/androidx/palette/graphics/Palette):Extract prominent colors from an image.
- [Firebase](https://firebase.google.com/): Google platform which helps build, manage, and grow their apps easily.
- [ViewPager2](https://developer.android.com/develop/ui/views/animations/screen-slide-2), [Dots Indicator](https://github.com/tommybuonomo/dotsindicator): Create cool On-boarding screens.
- [CircleImageView](https://github.com/hdodenhof/CircleImageView): A fast circular ImageView perfect for profile images.
- [Xplosion](https://github.com/BanDev/Xplosion): Twitter like animation for any view adapted for Kotlin.
- [EasyFlipView](https://github.com/wajahatkarim3/EasyFlipView): Create views with two sides with a flip animation.

## Download
You can download the APK by heading to [Releases](https://github.com/DanielIlievski/Pokepedia/releases).

## Architecture
**Pokepedia** utilizes the MVVM architecture and the Repository pattern, which follows the [Google's official architecture guidance](https://developer.android.com/topic/architecture).

![architecture](previews/architecture_layers.png)

**Pokepedia** follows a three-layer architecture comprising the UI layer, the data layer and the domain layer, where each layer has dedicated components with different responsibilities.<br>
Also, each layer follows [unidirectional event/data flow](https://developer.android.com/topic/architecture/ui-layer#udf), meaning:<br> 
<p align="center"><i>the UI emits events → the mediator (ViewModel) notifies the state change to the data layer → the data layer updates the application data → the ViewModel gets the updated data and updates the UI</i></p>

This makes the architecture loosely-coupled, which in turn increases the reusability of components and scalability of your app.

## Open API
Pokepedia uses the consumption-only [PokeAPI](https://pokeapi.co/), which offers a RESTful API interface containing extensive data on Pokémon.