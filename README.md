# PRODUCT-CATALOG-OFFLINE-CART

The application fetches products from the DummyJSON Products API, allows users to browse and search products, view product details, and manage a locally persisted shopping cart.

# Features

- Product listing from DummyJSON API
- Product image, name, price and rating
- Product search
- Product details
- Add products to cart
- Increase/decrease cart quantity
- Remove products from cart
- Total item count
- Total cart price
- Local cart persistence using Room Database
- Offline cart functionality
- Loading state
- Empty state
- API/network error handling
- Retry functionality

# Architecture

The application follows the MVVM (Model-View-ViewModel) architecture.

## Architecture Flow

UI
↓
ViewModel
↓
Repository
↓
Retrofit API / Room Database

# Responsibilities

**UI**
- Displays product and cart information
- Handles user interactions
- Observes ViewModel state

**ViewModel**
- Manages UI state
- Handles product search
- Communicates with the Repository
- Performs operations using Coroutines

**Repository**
- Acts as a single source of data
- Handles API and local database operations

**Retrofit**
- Handles REST API communication

**Room**
- Persists cart data locally

# Libraries Used

- Kotlin
- Android SDK
- AndroidX
- ViewModel
- LiveData
- Kotlin Coroutines
- Retrofit
- OkHttp
- Room Database
- Swipe Refresh Layout
- Glide

# Local Storage

Room Database is used to persist shopping cart items locally.
When a product is added to the cart, the cart information is stored in the local Room database.
The cart does not depend on the network after the product has been added.

The following cart operations work offline:
- View cart items
- Increase quantity
- Decrease quantity
- Remove items
- Calculate total item count
- Calculate total price
Therefore, closing and reopening the application does not remove the cart data.

# Important Design Decisions

## MVVM
MVVM was selected to separate UI, business logic and data access responsibilities.

## Repository Pattern
The Repository acts as the single source of data and keeps API/database implementation details away from the UI layer.

## Room for Cart Persistence
Cart data is stored locally using Room because the assessment requires the cart to remain fully functional without an internet connection.

## Local Product Search
Products are fetched from the API and retained in memory for search filtering. This avoids making a new API request for every character typed into the search field.

## Coroutines
Kotlin Coroutines are used for asynchronous API and database operations without blocking the main thread.

## Error Handling
The application handles:
- Network errors
- API failures
- Request timeout
- Empty product list
- Empty search results

A retry option is provided when appropriate.

# Setup / Build Instructions

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download the required dependencies.
4. Connect an Android device or start an Android Emulator.
5. Run the application using Android Studio.

No API key or additional account is required.

# Known Limitations

- Product data depends on the availability of the DummyJSON API.
- Product listing and product details require network access.
- The offline functionality is focused on the shopping cart as required by the assessment.
- No user authentication or payment functionality is included because they are outside the assessment requirements.
