package edu.piedpiper.uw.ischool.watsonchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView

class PersonalityNeeds : AppCompatActivity() {

    private lateinit var wv : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tone_chart)

        wv = findViewById(R.id.web)

        val data = intent.extras.getSerializable("data") as HashMap<String, ArrayList<Double>>

        var values : String = ""

        //println(data.keys)
        val keys : ArrayList<String> = ArrayList(data.keys)

        for (a in 0..keys.size - 1){
            val name : String = keys[a]
            val items : ArrayList<Double>? = data.get(name)

            val percentile : Double = items!![0]
            val raw : Double = items[1]

            values += "['" + name + "', " + raw + ", " + percentile + ", '" + name + "', " + percentile + "],"
        }

        println(values)

        val chart = "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      google.charts.load('current', {'packages':['corechart']});\n" +
                "      google.charts.setOnLoadCallback(drawSeriesChart);\n" +
                "\n" +
                "    function drawSeriesChart() {\n" +
                "\n" +
                "      var data = google.visualization.arrayToDataTable([\n" +
                "        ['ID', 'Score', 'Percentile', 'Name', 'Score'],\n" + values +
                "      ]);\n" +
                "\n" +
                "      var options = {\n" +
                "        title: 'Predicted Needs of the User',\n" +
                "        hAxis: {title: 'Score'},\n" +
                "        vAxis: {title: 'Percentile'},\n" +
                "        bubble: {textStyle: {fontSize: 11}}\n" +
                "      };\n" +
                "\n" +
                "      var chart = new google.visualization.BubbleChart(document.getElementById('series_chart_div'));\n" +
                "      chart.draw(data, options);\n" +
                "    }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"series_chart_div\" style=\"width: 900px; height: 500px;\"></div>\n" +
                "  </body>\n" +
                "</html>"

        val settings : WebSettings = wv.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.builtInZoomControls = true

        wv.loadDataWithBaseURL("file:///android_asset/", chart, "text/html", "utf-8", null )
    }
}