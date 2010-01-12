package gem.seek

/**
 * Abstract ApplicationContext contains methods with application scope.

 * Created by Gem Seeker on 7/18/2018.
 */
abstract class ApplicationContext : Context {

    /**
     * Creates and returns [FragmentManager] instance for the [Activity]. If a [FragmentManager] instance was already created,
     * it will return it instead.
     *
     * @return [FragmentManager] instance for the [Activity]
     */
    abstract fun getFragmentManager(activity: Activity): FragmentManager?
}
