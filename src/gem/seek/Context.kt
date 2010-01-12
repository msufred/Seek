package gem.seek

/**
 * A Context and it's derived are the only ones allowed to call/start another Activity.

 * @see Activity

 * Created by Gem Seeker on 4/19/2017.
 */
interface Context {
    /**
     * Starts new Activity. This method do all the instantiating, creating and starting an Activity.
     */
    fun startActivity(activity: Class<out Activity>)

    /**
     * Starts new Activity. This method assumes that Activity has been created.
     */
    fun startActivity(activity: Activity)

    /**
     * Starts new Activity from parent Activity. It is the same as [startActivity] but with additional task, setting
     * next Activity's parent Activity. Calling [Activity.onBackPressed] will start Activity's parent Activity.
     */
    fun startActivityFromParent(activity: Class<out Activity>, parentActivity:Activity)

    /**
     * Starts new Activity from parent Activity. This method assumes that the Activity has been created.
     */
    fun startActivityFromParent(activity: Activity, parentActivity: Activity)
}
