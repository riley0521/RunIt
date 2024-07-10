
# RunIt

A running tracker app that track your runs to achieve fitness milestones.


## Download

- Mobile App
- Wear App
## Screenshots

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)
## Tech Stack

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI Framework
- Google Libs
    - [Firebase Auth](https://firebase.google.com/docs/auth/android/start) - Used to authenticate users
    - [Firebase Firestore](https://firebase.google.com/docs/firestore/quickstart) - Used to persist run data
    - [Firebase Storage](https://firebase.google.com/docs/storage/android/start) - Used to save run related images
    - [Feature delivery](https://developer.android.com/guide/playcore/feature-delivery) - Used for dynamic feature (analytics feature)
- [Koin](https://insert-koin.io/docs/setup/koin/) - Dependency injection framework. We use this instead of Hilt because it's more flexible when we have dynamic feature.
- [Room](https://developer.android.com/training/data-storage/room) - Used to store data locally because our app is offline-first.
- [Vico Charts](https://github.com/patrykandpatrick/vico) - Used to display charts in analytics feature.
- [Compose Dialogs](https://github.com/maxkeppeler/sheets-compose-dialogs) - Used to display date range picker in compose.
- [Timber](https://github.com/JakeWharton/timber) - Logger library.
- [Splash Screen API](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate) - Used to support better splash screen in Android 12 and above.
- Testing
    - [AssertK](https://github.com/willowtreeapps/assertk) - Assertion in Kotlin
    - [MockK](https://github.com/mockk/mockk) - Mocking in Kotlin
    - [Turbine](https://github.com/cashapp/turbine) - Used to test Flow.
## Modularization

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)
## License

Copyright 2024 - Riley Farro

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

