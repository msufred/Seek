package gem.seek

/**
 * Abstract ApplicationContext contains methods with application scope.

 * Created by Gem Seeker on 7/18/2018.
 */
abstract class ApplicationContext : Context {
    abstract fun getFragmentManager(activity: Activity): FragmentManager?
}
