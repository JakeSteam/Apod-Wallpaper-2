package uk.co.jakelee.apodwallpaper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.jakelee.apodwallpaper.app.DeepLinkParser

class MainActivity : ActionBarActivity, AppCompatActivity() {

  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.navController
    val appBarConfiguration = AppBarConfiguration(setOf(
      R.id.navigation_item,
      R.id.navigation_browse,
      R.id.navigation_settings,
      R.id.navigation_more
    ))
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)

    tryNavigateDeepLink(navController, intent)
  }

  override fun showActionBar() = supportActionBar?.show() ?: Unit

  override fun hideActionBar() = supportActionBar?.hide() ?: Unit

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp() || super.onSupportNavigateUp()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    tryNavigateDeepLink(navHostFragment.navController, intent)
  }

  private fun tryNavigateDeepLink(navController: NavController, intent: Intent?) {
    DeepLinkParser(intent).parse()?.let {
      when (it.action) {
        DeepLinkParser.LinkActions.DAY -> navController.navigate(NavigationDirections.openApod(null, it.parameter))
        DeepLinkParser.LinkActions.BROWSE -> navController.navigate(R.id.navigation_browse)
        DeepLinkParser.LinkActions.SETTINGS -> navController.navigate(R.id.navigation_settings)
        DeepLinkParser.LinkActions.MORE -> navController.navigate(R.id.navigation_more)
      }
    }
  }

}