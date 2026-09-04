package com.bloom.gps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*

class LocationTrackerService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var dernierEnvoi: Location? = null
    private var numeroCible: String? = null
    private var estEnCours = false

    companion object {
        const val CHANNEL_ID = "BloomGPS_Service"
        const val NOTIFICATION_ID = 1001
        const val ACTION_DEMARRER = "DEMARRER"
        const val ACTION_ARRETER = "ARRETER"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        creerCanalNotification()
        configurerLocationRequest()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra("commande")) {
            ACTION_ARRETER -> arreterSuivi()
            else -> {
                val cmd = intent?.getStringExtra("commande") ?: ""
                if (cmd.startsWith("DEMARRER:")) {
                    numeroCible = cmd.removePrefix("DEMARRER:")
                    demarrerSuivi()
                }
            }
        }
        return START_STICKY
    }

    private fun configurerLocationRequest() {
        locationRequest = LocationRequest.Builder(5000L)
            .setMinUpdateDistanceMeters(10f)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    private fun demarrerSuivi() {
        if (estEnCours) return
        estEnCours = true

        startForeground(NOTIFICATION_ID, creerNotification())

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            Log.d("BloomGPS", "✅ Suivi démarré → $numeroCible")
        } catch (e: SecurityException) {
            Log.e("BloomGPS", "❌ Permission GPS manquante", e)
            arreterSuivi()
        }
    }

    private fun arreterSuivi() {
        if (!estEnCours) return
        estEnCours = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        dernierEnvoi = null
        Log.d("BloomGPS", "⏹️ Suivi arrêté")
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            traiterNouvellePosition(location)
        }
    }

    private fun traiterNouvellePosition(location: Location) {
        dernierEnvoi?.let { derniere ->
            val distance = location.distanceTo(derniere)
            if (distance < 10f) return
        }

        dernierEnvoi = location

        val vitesse = if (location.hasSpeed()) location.speed * 3.6f else 0f
        envoyerPositionParSMS(location.latitude, location.longitude, vitesse)

        val update = Intent("BLOOMGPS_MA_POSITION")
        update.setPackage(packageName)
        update.putExtra("lat", location.latitude)
        update.putExtra("lon", location.longitude)
        update.putExtra("vitesse", vitesse)
        sendBroadcast(update)
    }

    private fun envoyerPositionParSMS(lat: Double, lon: Double, vitesse: Float) {
        val numero = numeroCible ?: return
        try {
            val sms = android.telephony.SmsManager.getDefault()
            val message = "BLOOMGPS:$lat,$lon,$vitesse"
            sms.sendDataMessage(numero, null, 50006.toShort(), message.toByteArray(), null, null)
            Log.d("BloomGPS", "📤 Position envoyée : $lat, $lon → $numero")
        } catch (e: Exception) {
            Log.e("BloomGPS", "❌ Erreur envoi SMS", e)
        }
    }

    private fun creerCanalNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Bloom GPS Suivi",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(canal)
        }
    }

    private fun creerNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bloom GPS — Suivi actif")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .setOngoing(true)
            .setContentIntent(
                android.app.PendingIntent.getActivity(
                    this,
                    0,
                    packageManager.getLaunchIntentForPackage(packageName),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE else 0
                )
            )
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        arreterSuivi()
    }
}
