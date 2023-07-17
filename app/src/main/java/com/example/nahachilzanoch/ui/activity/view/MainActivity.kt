package com.example.nahachilzanoch.ui.activity.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nahachilzanoch.appComponent
import com.example.nahachilzanoch.databinding.ActivityMainBinding
import com.example.nahachilzanoch.ui.activity.TaskListViewModel
import com.example.nahachilzanoch.ui.notifications.reminderNotificationChannelID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TaskListViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding

    val mainActivityComponent by lazy {
        appComponent.mainActivityComponent().manufacture()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent.inject(this)

        super.onCreate(savedInstanceState)

        setupOnNetworkAvailableCallback()
        collectErrors()
        createReminderNotificationsChannel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun createReminderNotificationsChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            reminderNotificationChannelID,
            "Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }



    private fun collectErrors() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.errorStringFlow.collect {
                if (it != null) {
                    showToast(it)
                }
            }
        }
    }

    private fun setupOnNetworkAvailableCallback() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.updateFromRemote()
                super.onAvailable(network)
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private suspend fun showToast(text: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                application.applicationContext,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}