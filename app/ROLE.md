Role field for users

What changed
- New `role` string field is written to the Firestore user document when a user registers (email signup) or signs in with Google.
- Default value written: `"customer"`.
- The app UI now shows the admin views when the user's `role` is exactly `"admin"`. Otherwise it shows the regular customer view (purchase history, etc.).

Where to look in code
- Registration / Google sign-in: `app/src/main/java/com/example/techport/ui/login/authy/AuthViewModel.kt`
- Role loading and cached role: `app/src/main/java/com/example/techport/ui/home/HomeViewModel.kt` (property `userRole`)
- Admin checks (replaced hardcoded UID):
  - `app/src/main/java/com/example/techport/ui/main/MainScreen.kt`
  - `app/src/main/java/com/example/techport/ui/home/HomeScreen.kt`
  - `app/src/main/java/com/example/techport/ui/home/ProductDetailScreen.kt`

How to promote a user to admin
1. Open the Firebase Console for your project.
2. Navigate to Firestore Database -> `user` collection.
3. Find the document with the user's UID.
4. Edit or add the field `role` with the string value `admin`.

Notes and testing
- After registering, a user's document will contain `role: "customer"` by default.
- The app reads the role on startup and on auth state changes. If you change a user's role in Firestore while they're signed in, they may need to re-login or you can trigger a reload by signing out and signing back in.

Build note (local environment)
- I attempted a Gradle assemble to validate compilation, but the build failed due to the local Java/Gradle environment (the JVM reported an unsupported Java version value). This is an environment issue, not a code error. To build locally, ensure you have a supported JDK (e.g., 11 or 17) and set `JAVA_HOME` accordingly.

If you'd like, I can:
- Add an admin promotion UI (protected) to promote users from within the app.
- Add unit/instrumentation tests for role-based UI behavior.


