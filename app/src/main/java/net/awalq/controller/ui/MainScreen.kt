package net.awalq.controller.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.awalq.controller.data.AppDatabase
import net.awalq.controller.data.Voucher
import net.awalq.controller.portal.CaptivePortalServer

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).voucherDao() }
    val scope = rememberCoroutineScope()
    var vouchers by remember { mutableStateOf<List<Voucher>>(emptyList()) }
    var server by remember { mutableStateOf<CaptivePortalServer?>(null) }
    var serverStatus by remember { mutableStateOf("متوقف") }

    LaunchedEffect(Unit) {
        dao.getAll().collect { vouchers = it }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("عولق نت", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("لوحة التحكم - $serverStatus", fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    if (server == null) {
                        server = CaptivePortalServer(context).also { it.start() }
                        serverStatus = "يعمل على 8080"
                    }
                }
            }) { Text("تشغيل") }
            Button(onClick = { server?.stop(); server = null; serverStatus = "متوقف" }) { Text("إيقاف") }
            Button(onClick = {
                scope.launch {
                    val code = "AW${System.currentTimeMillis().toString().takeLast(6)}"
                    dao.insert(Voucher(code = code, durationMinutes = 60, dataLimitMb = 500))
                }
            }) { Text("توليد كرت") }
        }
        Spacer(Modifier.height(16.dp))
        Text("الكروت (${vouchers.size})", fontWeight = FontWeight.Bold)
        LazyColumn(Modifier.weight(1f)) {
            items(vouchers) { v ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("كود: ${v.code}", fontWeight = FontWeight.Bold)
                        Text("${v.durationMinutes} دقيقة | ${v.dataLimitMb} MB")
                    }
                }
            }
        }
    }
}
