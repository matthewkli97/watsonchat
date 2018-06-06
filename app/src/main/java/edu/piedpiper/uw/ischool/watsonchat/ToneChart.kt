package edu.piedpiper.uw.ischool.watsonchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.webkit.WebSettings
import android.webkit.WebView

class ToneChart : AppCompatActivity() {

    private lateinit var wv : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tone_chart)
        wv = findViewById(R.id.web)

        val extras = intent.extras
        val tone = extras.getSerializable("tones") as ArrayList<Array<String>>

        var categories : String = ""

        for (a in 0..(tone.size - 1)) {
            //println(tone[a][0])
            categories += "['" + tone[a][0] + "', " + tone[a][1].toDouble() * 100 + "], "
        }

        val chart : String = "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      google.charts.load(\"current\", {packages:[\"corechart\"]});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "      function drawChart() {\n" +
                "        var data = google.visualization.arrayToDataTable([\n" +
                "          ['Tone', 'Tone Score'],\n" + categories +
                "        ]);\n" +
                "\n" +
                "        var options = {\n" +
                "          title: 'Personality Assessment Tones',\n" +
                "          pieHole: 0.4,\n" +
                "        };\n" +
                "\n" +
                "        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"donutchart\" style=\"width: 900px; height: 500px;\"></div>\n" +
                "  </body>\n" +
                "</html>"

        val settings : WebSettings = wv.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.builtInZoomControls = true
        //settings.defaultZoom

        //wv.requestFocusFromTouch()
        //wv.setInitialScale(1)

        wv.loadDataWithBaseURL("file:///android_asset/", chart, "text/html", "utf-8", null )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}