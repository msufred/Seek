# Seek
Seek is a library for JavaFX applications based on Android activity lifecycle. A single or group of task is encapsulated in an Activity.

Each activity has it's own lifecycle. Seek's Activity has seven states:
* CREATED
* STARTED
* PAUSED
* RESUMED
* STOPPED
* DESTROYED
* NOT_DEFINED.

On each of these states, you can define custom logic operations. You can define custom logic operations in any of these methods:
* onCreate()
* onStart()
* onResume()
* onPause()
* onStop()
* onDestroy()

One of the important things to remember in using Seek is that Activity objects MUST NOT be initialized manually (ex. val activity = MyActivity()). Seek application will be responsible for creating, initializing and displaying Activity objects. Thus, we can only worry about on how the Activity will behave on each of it states. Here are some of the recommendations on what you can put in each of Activity's states.

onCreate()
  -- Called 
