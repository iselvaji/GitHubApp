# GitHubApp
Android application for GitHub user search and view user details

# Architecture

<img width="464" alt="image" src="https://user-images.githubusercontent.com/28477412/160380926-8f2d4e0e-008b-4d42-9f24-a87ac0aa0be2.png">

UI observing the live data for the main list as received from the ViewModel-> Repo-> PagingSource-> RemoteDataSource


# Components used

Programming language - Kotlin

Design - Android Model View View Model design pattern used

Coroutines - Is light wight threads for asynchronous programming

Flow - Handle the stream of data asynchronously that executes sequentially.

Android Architecture Components - Collection of libraries that help you design robust, testable, and maintainable apps.

LiveData - Data objects that notify views when the underlying data changes.

ViewModel - Stores UI-related data that isn't destroyed on UI changes.

ViewBinding - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.

Dependency Injection - Hilt-android Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.

Network - Retrofit, OkHttp library used

Moshi - Java serialization/deserialization library to convert Java Objects into JSON and back

Coil -  Image loading and caching library for Android

Paging - Paging 3 library helps us to load and display pages of data from a larger dataset from local storage or over network

Material Design - Material is a design UI elements 

Network change listener for observing mobile network changes

Testing - Espresso, Mockito, Hilt testing

# UI Design

Search user (Enter user name in search bar and click search icon) then Github User list is fetched from remote source and displayed as shown below. 

![image](https://user-images.githubusercontent.com/28477412/160381965-995c71f3-7dfb-437d-bb6f-5b3afdb8c2b3.png)

On click of any item in the list, then user will be navigated to Details screen.

![image](https://user-images.githubusercontent.com/28477412/160382061-82d3dd87-2215-400d-a826-137d40f2678c.png)

In case of network unavailabilty Error message is displayed.

![image](https://user-images.githubusercontent.com/28477412/160382248-b7d62a88-d06e-4d89-810b-dd92e9c2e9d9.png)

