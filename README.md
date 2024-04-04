## Breif introduction

This app is purely built using Kotlin, following the core design principles of Android and adopting the MVVM framework.

- ### Demo Video link -> [Click here](https://drive.google.com/file/d/1ZZjnygGkzlN2cPHBJG4nSvaDa74AzGfo/view?usp=sharing)
- ### Release apk -> [Click here](https://github.com/pahadi777/News-App/blob/master/app/release/News-App.apk)

- ### Screenshot ->

<img height="400" alt="Home" src="https://github.com/pahadi777/News-App/assets/90754518/3fe470a5-6928-4914-9301-e7f4711e7337">


## Key Features

- Implemented the app's layouts using Material UI.
- Used DataStore instead of SharedPreferences to save our preferred news sorting option, as DataStore provides an extra layer of security with its EncryptedDataStore module, ensuring the protection of sensitive information.
- As this app follows the MVVM design principle, we've chosen to use a combination of repository, view model, and LiveData for handling API calls, moving away from the older approach of using AsyncTasks and callbacks. This modern approach helps us organize and manage code more effectively.
- Implemented Firebase Cloud Messaging (FCM) to send notifications to mobile devices remotely
