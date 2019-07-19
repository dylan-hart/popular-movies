# Popular Movies
This is an Android application written in Java that displays popular movies from a free database API. I've written it as part of the Udacity Android Developer Nanodegree program.

#### Adding an API key
To build this application for yourself, you must provide your own API key for making HTTP requests from [The Movie DB](https://www.themoviedb.org/), as this repo does not provide one. The steps below explain how you can do this:
1. Create a file named "config" in the project's root directory.
2. Include the following text in the file, replacing _YOUR_API_KEY_ with your own API key from themoviedb.org:
```
tmdbApiKey=YOUR_API_KEY
```
3. Build the project. Gradle will read this file and make your API key available through its BuildConfig.API_KEY_TMDB field.
