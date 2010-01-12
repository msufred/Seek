package gem.seek

/**
 * <p>StatefulActivity interface that implements Stateful is an interface made for Activity. Given with Stateful methods,
 * StatefulActivity implements additional methods for Activity.
 *
 * @see Stateful
 *
 * Created by Gem Seeker on 4/22/2017.
 */
interface StatefulActivity : Stateful {
    fun onBackPressed()
}