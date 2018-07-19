package gem.seek

/**
 * A Context and it's derived are the only ones allowed to call/start another Activity.

 * @see Activity

 * Created by Gem Seeker on 4/19/2017.
 */
interface Context {
    fun startActivity(activity: Class<out Activity>)
    fun startActivity(activity: Activity)
}
