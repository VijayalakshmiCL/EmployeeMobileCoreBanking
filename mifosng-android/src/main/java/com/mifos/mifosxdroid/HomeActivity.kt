package com.mifos.mifosxdroid

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.test.espresso.IdlingResource
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingActivity
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment
import com.mifos.mifosxdroid.online.GenerateCollectionSheetActivity
import com.mifos.mifosxdroid.online.RunReportsActivity
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxPendingTasksActivity
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment
import com.mifos.mifosxdroid.online.search.SearchFragment
import com.mifos.utils.Constants
import com.mifos.utils.EspressoIdlingResource
import com.mifos.utils.PrefManager


/**
 * Created by shashankpriyadarshi on 19/06/20.
 */
open class HomeActivity : MifosBaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val PERMISSION_BLUETOOTH = 1

    @RequiresApi(Build.VERSION_CODES.S)
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )
    @RequiresApi(Build.VERSION_CODES.S)
    private val PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )

    @BindView(R.id.navigation_view)
    lateinit  var mNavigationView: NavigationView

    @BindView(R.id.drawer)
    lateinit var mDrawerLayout: DrawerLayout

    private var menu: Menu? = null

    private var itemClient = true
    private var itemCenter = true
    private var itemGroup = true

    var navController: NavController? = null
    private var mNavigationHeader: View? = null
    var userStatusToggle: SwitchCompat? = null
    private var doubleBackToExitPressedOnce = false
    var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mDrawerLayout = findViewById(R.id.drawer)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mNavigationView = findViewById(R.id.navigation_view)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermissions()
            //getExternalStoragePermission(PERMISSIONS_STORAGE)
            //getExternalStoragePermission(PERMISSIONS_LOCATION)
        }
        // navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder()
                .setDrawerLayout(mDrawerLayout)
                .build()
        // NavigationUI.setupActionBarWithNavController(this, navController!!, appBarConfiguration!!)
        // NavigationUI.setupWithNavController(mNavigationView, navController!!)
        if (savedInstanceState == null) {
            val fragment: Fragment = SearchFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container_a, fragment).commit()
            supportActionBar!!.setTitle(R.string.dashboard)
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    openFragment(SearchFragment())
                    supportActionBar!!.setTitle(R.string.dashboard)
                }
                R.id.navigation_client_list -> {
                    openFragment(ClientListFragment())
                    supportActionBar!!.setTitle(R.string.clients)
                }
                R.id.navigation_center_list -> {
                    openFragment(CenterListFragment())
                    supportActionBar!!.setTitle(R.string.title_activity_centers)
                }
                R.id.navigation_group_list -> {
                    openFragment(GroupsListFragment())
                    supportActionBar!!.setTitle(R.string.title_center_list)
                }
            }
            true
        }
        setupNavigationBar()
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private fun checkPermissions() {
        val permission1 =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        val permission2 =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                1
            )
        }
    }

//    // @RequiresApi(Build.VERSION_CODES.S)
//    private fun getExternalStoragePermission(permissions: Array<String>) {
//        var count = 0;
//        for (permission in permissions){
//            count++;
//            if (!permissionGranted || count!= permissions.size) {
//                //Permission not granted
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        permission
//                    )
//                ) {
//                    //Can ask user for permission
//                    ActivityCompat.requestPermissions(
//                        this, arrayOf(permission),
//                        1
//                    )
//                } else {
//
////                if (userAskedPermissionBefore) {
////                    //If User was asked permission before and denied
////                    val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
////                    alertDialogBuilder.setTitle("Permission needed")
////                    alertDialogBuilder.setMessage("Storage permission needed for accessing photos")
////                    alertDialogBuilder.setPositiveButton("Open Setting",
////                        DialogInterface.OnClickListener { dialogInterface, i ->
////                            val intent = Intent()
////                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
////                            val uri: Uri = Uri.fromParts(
////                                "package", this.packageName(),
////                                null
////                            )
////                            intent.data = uri
////                            this.startActivity(intent)
////                        })
////                    alertDialogBuilder.setNegativeButton("Cancel",
////                        DialogInterface.OnClickListener { _, i ->
////                            Log.d(
////                                "Cancelled",
////                                "onClick: Cancelling"
////                            )
////                        })
////                    val dialog: AlertDialog = alertDialogBuilder.create()
////                    dialog.show()
////                } else {
//                    //If user is asked permission for first time
//                    ActivityCompat.requestPermissions(
//                        this, arrayOf(permission),
//                        1
//                    )
//                    //}
//                }
//            } else {
//                permissionGranted = true
//            }
//        }
//
//    }

    private fun setMenuCreateGroup(isEnabled: Boolean) {
        if (menu != null) {
            //position of mItem_create_new_group is 2
            menu!!.getItem(2).isEnabled = isEnabled
        }
    }

    private fun setMenuCreateCentre(isEnabled: Boolean) {
        if (menu != null) {
            //position of mItem_create_new_centre is 1
            menu!!.getItem(1).isEnabled = isEnabled
        }
    }

    private fun setMenuCreateClient(isEnabled: Boolean) {
        if (menu != null) {
            //position of mItem_create_new_client is 0
            menu!!.getItem(0).isEnabled = isEnabled
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // ignore the current selected item
        /*if (item.isChecked()) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        }*/
        clearFragmentBackStack()
        val intent = Intent()
        when (item.itemId) {
            R.id.item_checker_inbox -> {
                intent.setClass(this, CheckerInboxPendingTasksActivity::class.java);
                startActivity(intent);
            }
            R.id.item_path_tracker -> {
                intent.setClass(applicationContext, PathTrackingActivity::class.java)
                startNavigationClickActivity(intent)
            }
            R.id.item_offline -> {
                replaceFragment(OfflineDashboardFragment.newInstance(), false, R.id.container_a)
                supportActionBar!!.setTitle(R.string.offline)
            }
            R.id.individual_collection_sheet -> {
                intent.setClass(this, GenerateCollectionSheetActivity::class.java)
                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_INDIVIDUAL)
                startActivity(intent)
            }
            R.id.collection_sheet -> {
                intent.setClass(this, GenerateCollectionSheetActivity::class.java)
                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_COLLECTION)
                startActivity(intent)
            }

            R.id.item_printers -> {
                intent.setClass(this, PrintersActivity::class.java)
                startActivity(intent)
            }

            R.id.item_settings -> {
                intent.setClass(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.runreport -> {
                intent.setClass(this, RunReportsActivity::class.java)
                startActivity(intent)
            }
            R.id.about -> {
                intent.setClass(this, AboutActivity::class.java);
                startActivity(intent);
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * This SwitchCompat Toggle Handling the User Status.
     * Setting the User Status to Offline or Online
     */
    fun setupUserStatusToggle() {
        userStatusToggle = mNavigationHeader!!.findViewById(R.id.user_status_toggle)
        if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
            userStatusToggle?.isChecked = true
        }
        userStatusToggle?.setOnClickListener(View.OnClickListener {
            if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
                PrefManager.setUserStatus(Constants.USER_ONLINE)
                userStatusToggle!!.isChecked = false
            } else {
                PrefManager.setUserStatus(Constants.USER_OFFLINE)
                userStatusToggle!!.isChecked = true
            }
        })
    }

    fun startNavigationClickActivity(intent: Intent?) {
        val handler = Handler()
        handler.postDelayed({ startActivity(intent) }, 500)
    }

    /**
     * downloads the logged in user's username
     * sets dummy profile picture as no profile picture attribute available
     */
    private fun loadClientDetails() {
        // download logged in user
        val loggedInUser = PrefManager.getUser()
        val textViewUsername = ButterKnife.findById<TextView>(mNavigationHeader!!, R.id.tv_user_name)
        textViewUsername.text = loggedInUser.username

        // no profile picture credential, using dummy profile picture
        val imageViewUserPicture = ButterKnife
                .findById<ImageView>(mNavigationHeader!!, R.id.iv_user_picture)
        imageViewUserPicture.setImageResource(R.drawable.ic_dp_placeholder)
    }

    override fun onBackPressed() {
        // check if the nav mDrawer is open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                setMenuCreateClient(true)
                setMenuCreateCentre(true)
                setMenuCreateGroup(true)
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    @get:VisibleForTesting
    val countingIdlingResource: IdlingResource
        get() = EspressoIdlingResource.getIdlingResource()

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController!!, appBarConfiguration!!)
    }

    protected fun setupNavigationBar() {
        mNavigationHeader = mNavigationView.getHeaderView(0)
        setupUserStatusToggle()
        mNavigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)

        // setup drawer layout and sync to toolbar
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                setUserStatus(userStatusToggle)
                hideKeyboard(mDrawerLayout)
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (slideOffset != 0f) super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // make an API call to fetch logged in client's details
        loadClientDetails()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    fun openFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_a, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}