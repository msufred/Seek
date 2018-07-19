package gem.seek

/**
 * Stateful interface allows it's derived classes to invoke methods according to their state.
 *
 * Created by Gem Seeker on 7/18/2018.
 */
interface Stateful {
    fun onCreate()
    fun onStart()
    fun onPause()
    fun onResume()
    fun onStop()
    fun onDestroy()
}
