package com.example.techport.ui.map

import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.techport.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private data class Shop(val name: String, val pos: LatLng, val address: String)

private val demoShops = listOf(
    Shop("TechPort Lille Centre", LatLng(50.6366, 3.0630), "12 Rue Nationale, Lille"),
    Shop("TechPort Wazemmes",    LatLng(50.6239, 3.0390), "30 Pl. de la Nlle Aventure, Lille"),
    Shop("TechPort Euralille",   LatLng(50.6382, 3.0755), "100 Euralille, Lille")
)

private data class RouteResult(
    val points: List<LatLng>,
    val distanceText: String,
    val durationText: String
)

@Composable
fun MapScreen() {
    val directionsKey = stringResource(id = R.string.google_directions_key)

    // Start at Junia
    val userJunia = LatLng(50.6320, 3.0214)
    var myLoc by remember {
        mutableStateOf<Location?>(Location("fixed").apply {
            latitude = userJunia.latitude
            longitude = userJunia.longitude
        })
    }

    // Camera
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userJunia, 14f)
    }
    LaunchedEffect(Unit) {
        camera.animate(CameraUpdateFactory.newLatLngZoom(userJunia, 14f), 600)
    }

    // UI state
    var selected by remember { mutableStateOf(0) }
    var route by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distance by remember { mutableStateOf<String?>(null) }
    var duration by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = camera,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false, compassEnabled = true)
        ) {
            demoShops.forEach { s ->
                Marker(
                    state = rememberMarkerState(position = s.pos),
                    title = s.name,
                    snippet = s.address
                )
            }
            if (route.isNotEmpty()) Polyline(points = route)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Better chips row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(demoShops) { i, s ->
                    OutlinedButton(
                        onClick = { selected = i },
                        shape = MaterialTheme.shapes.extraLarge,
                        border = BorderStroke(1.dp, if (selected == i) Color.Black else Color.LightGray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text(s.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(demoShops[selected].address, style = MaterialTheme.typography.bodySmall)

            // Distance + time
            if (distance != null && duration != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Approx: $distance • $duration",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Draw Route - styled like the login primary button (black, extraLarge, shadow)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            val src = myLoc?.let { LatLng(it.latitude, it.longitude) }
                                ?: run { error = "Location unavailable"; return@Button }
                            val dst = demoShops[selected].pos
                            loading = true; error = null
                            scope.launch {
                                val res = fetchRoute(src, dst, directionsKey)
                                if (res == null) {
                                    route = emptyList()
                                    distance = null
                                    duration = null
                                    error = "No route"
                                } else {
                                    route = res.points
                                    distance = res.distanceText
                                    duration = res.durationText
                                    // focus camera on route end
                                    camera.animate(
                                        CameraUpdateFactory.newLatLngZoom(dst, 14f),
                                        600
                                    )
                                }
                                loading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, shape = MaterialTheme.shapes.extraLarge),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) { Text(if (loading) "Loading…" else "Draw Route") }
                }

                // Clear - default outlined button (no shadow)
                OutlinedButton(
                    onClick = {
                        route = emptyList()
                        distance = null
                        duration = null
                        error = null
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Clear") }
            }

            // Start Journey -> Google Maps
            if (route.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                val dst = demoShops[selected].pos
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        val gmmUri =
                            Uri.parse("google.navigation:q=${dst.latitude},${dst.longitude}&mode=d")
                        val intent = Intent(Intent.ACTION_VIEW, gmmUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Start Journey in Google Maps") }
            }

            if (error != null) {
                Spacer(Modifier.height(6.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private suspend fun fetchRoute(
    origin: LatLng,
    dest: LatLng,
    apiKey: String
): RouteResult? = withContext(Dispatchers.IO) {
    val url = "https://maps.googleapis.com/maps/api/directions/json" +
            "?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&mode=driving&key=$apiKey"

    val resp = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
    if (!resp.isSuccessful) return@withContext null
    val body = resp.body?.string() ?: return@withContext null

    val root = JSONObject(body)
    val routes = root.optJSONArray("routes") ?: return@withContext null
    if (routes.length() == 0) return@withContext null

    val route0 = routes.getJSONObject(0)
    val legs = route0.getJSONArray("legs")
    val leg0 = legs.getJSONObject(0)
    val distanceText = leg0.getJSONObject("distance").getString("text")
    val durationText = leg0.getJSONObject("duration").getString("text")
    val encoded = route0.getJSONObject("overview_polyline").getString("points")
    val points = PolyUtil.decode(encoded)

    RouteResult(points, distanceText, durationText)
}
