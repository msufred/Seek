# Seek
Seek is a library for JavaFX applications based on Android activity lifecycle. A single or group of task is encapsulated in an `Activity`.

Each activity has it's own lifecycle. Seek's `Activity` has seven states:
* `CREATED`
* `STARTED`
* `PAUSED`
* `RESUMED`
* `STOPPED`
* `DESTROYED`
* `NOT_DEFINED`

On each of these states, you can define custom logic operations. You can define custom logic operations in any of these methods:
* `onCreate()`
* `onStart()`
* `onResume()`
* `onPause()`
* `onStop()`
* `onDestroy()`

One of the important things to remember in using Seek is that Activity objects MUST NOT be initialized manually (ex. `val activity = MyActivity()`). Seek application will be responsible for creating, initializing and displaying `Activity` objects. Thus, we can only worry about on how the `Activity` will behave on each of it states. Here are some of the recommendations on what you can put in each of `Activity`'s states.

### `onCreate()`
`onCreate()` is called after the `Activity` is created (or instantiated). This is the best place to created objects, instantiate fields and create listeners.
`Activity` is in `CREATED` state after this method is invoked.

### `onStart()`
`onStart()` is called right after `onCreate` and `onStop()`. Operations like opening a database can be put in here. If the `Activity` came from `STOPPED` state (when `onStop()` was called), this is the best place to start or re-initialize resources.
`Activity` is in `STARTED` state after this method is invoked.

### `onResume()`
`onResume()` is called after `onStart()` and `onPause()`. Logic like refreshing layout contents (like text content of text fields), querying database, resuming downloads or resuming streams can be put in here.
`Activity` is in `RESUMED` state after this method is invoked.

### `onPause()`
`onPause()` is called when the `Activity` is requested to be moved to the background by the client (user). This can happen when another `Activity` is requested by the user or when the application is requested to terminate. Operations like terminating connections, pausing downloads and streams can be put in here.
`Activity` is in `PAUSED` state after this method is invoked.

### `onStop()`
`onStop()` is called when application is requested to terminate or when purposely closed by the application due to errors or by the code. Closing database, stopping downloads and streams can be done here.
`Activity` is in `STOPPED` state after this method is invoked.

### `onDestroy()`
`onDestroy()` is called when application is requested to terminated and after `onStop()`. Clean up operations like freeing up resources can be put in here.
`Activity` is in `DESTROYED` state after this method is invoked.

# Example

Here's an example of how to use and start a Seek application:

```
class MainActivity: Activity() {
  
  @FXML private lateinit var button: Button
  
  override fun onCreate() {
    super.onCreate()
    val loader = FXMLLoader(MainActivity::class.java.getResource("activity_main.fxml"))
    setContentView(loader)
    
    button.setOnAction ({
      println("Hello Seek World!")
    })
  }
  
}
```

```
class SeekTest: Application() {

  override fun start(primaryStage: Stage) {
    val seekApplication = SeekApplication()
    seekApplication.initialize(primaryStage, "Seek Test", 480.0, 720.0)
    seekApplication.startActivity(MainActivity::class.java)
    seekApplication.show()
  }
  
  companion object {
    @JvmStatic fun main(args: Array<String>) {
      Application.launch(SeekTest::class.java, *args)
    }
  }
}
```
