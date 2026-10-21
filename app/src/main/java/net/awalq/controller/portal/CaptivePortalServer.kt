package net.awalq.controller.portal

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.runBlocking
import net.awalq.controller.data.AppDatabase

class CaptivePortalServer(private val context: Context, port: Int = 8080) : NanoHTTPD(port) {
    private val dao = AppDatabase.getInstance(context).voucherDao()

    override fun serve(session: IHTTPSession): Response = when (session.uri) {
        "/login" -> handleLogin(session)
        else -> serveLoginPage()
    }

    private fun serveLoginPage(): Response {
        val html = """
            <!DOCTYPE html>
            <html dir="rtl" lang="ar">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>عولق نت</title>
              <style>
                *{box-sizing:border-box;margin:0;padding:0}
                body{font-family:'Segoe UI',sans-serif;background:linear-gradient(135deg,#0D47A1,#1976D2,#42A5F5);min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px}
                .card{background:rgba(255,255,255,0.97);border-radius:24px;padding:40px 30px;max-width:400px;width:100%;box-shadow:0 20px 60px rgba(0,0,0,0.3);text-align:center}
                .logo{font-size:32px;font-weight:bold;color:#0D47A1;margin-bottom:8px}
                .subtitle{color:#666;margin-bottom:30px;font-size:14px}
                input{width:100%;padding:14px 18px;border:2px solid #E0E0E0;border-radius:12px;font-size:16px;margin-bottom:16px}
                input:focus{outline:none;border-color:#1976D2}
                button{width:100%;padding:14px;background:linear-gradient(135deg,#0D47A1,#1976D2);color:#fff;border:none;border-radius:12px;font-size:16px;font-weight:bold;cursor:pointer}
                .footer{margin-top:20px;font-size:12px;color:#999}
              </style>
            </head>
            <body>
              <div class="card">
                <div class="logo">🌐 عولق نت</div>
                <div class="subtitle">Awalq Net - بوابة الاتصال</div>
                <form method="POST" action="/login">
                  <input type="text" name="code" placeholder="أدخل كود الكرت" required autocomplete="off">
                  <button type="submit">تسجيل الدخول</button>
                </form>
                <div class="footer">© 2026 عولق نت</div>
              </div>
            </body>
            </html>
        """.trimIndent()
        return newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", html)
    }

    private fun handleLogin(session: IHTTPSession): Response {
        val map = HashMap<String, String>()
        session.parseBody(map)
        val code = map["code"] ?: ""
        val v = runBlocking { dao.getByCode(code) }
        return if (v != null && v.isActive) {
            val html = "<html><body style='text-align:center;padding:50px;font-family:sans-serif;'>" +
                "<h1>✅ تم الدخول</h1><p>مرحباً بك في عولق نت</p>" +
                "<p>المتبقي: ${v.durationMinutes - v.usedMinutes} دقيقة</p></body></html>"
            newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", html)
        } else {
            newFixedLengthResponse(Response.Status.UNAUTHORIZED, "text/html; charset=utf-8",
                "<html><body style='text-align:center;padding:50px;'><h1>❌ كرت غير صالح</h1></body></html>")
        }
    }
}
