package edu.piedpiper.uw.ischool.watsonchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView

class PersonalityConsumption : AppCompatActivity() {

    private lateinit var wv : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tone_chart)

        wv = findViewById(R.id.web)

        val data = intent.extras.getSerializable("data") as HashMap<String, Map<String, Double>>

        var topics : String = ""
        var subtopics : String = ""

        var count : Int = data.keys.size + 1
        for (a in 0..data.keys.size - 1){
            var str = data.keys.elementAt(a);
            str = str.removePrefix("consumption_preferences_")
            str = str.replace('_', ' ')
            //topics += "[" + (a + 1) + ", '" + str + "', 0, 1, 0],\n"
            topics += "['" + str + "', 'You', ''],"

            val map : HashMap<String, Double> = data.get(data.keys.elementAt(a)) as HashMap<String, Double>
            for (b in 0..map.keys.size - 1){
                //println(map.keys.elementAt(b))

                val value : Double = map[map.keys.elementAt(b)] as Double
                if (value == 0.0){
                    var temp = map.keys.elementAt(b)
                    temp = temp.replace("Likely", "unlikely")

                    subtopics += "['" + temp + "', '" + str + "', ''],"
                }
                else if (value == 1.0){
                   // println(map.keys.elementAt(b) + value)
                    var temp = map.keys.elementAt(b)
                    temp = temp.replace("Likely", "likely")

                    subtopics += "['" + temp + "', '" + str + "', ''],"
                }
            }
        }

        println(topics)
        println(subtopics)

        val chart : String = "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      google.charts.load('current', {packages:[\"orgchart\"]});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      function drawChart() {\n" +
                "        var data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Characteristic');\n" +
                "        data.addColumn('string', 'Prediction'); data.addColumn('string', 'ToolTip');\n" +
                "\n" +
                "        // For each orgchart box, provide the name, manager, and tooltip to show.\n" +
                "        data.addRows([" + topics + subtopics +
                "        ]);\n" +
                "\n" + "var options = {allowCollapse: true }" +
                "        // Create the chart.\n" +
                "        var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));\n" +
                "        // Draw the chart, setting the allowHtml option to true for the tooltips.\n" +
                "        chart.draw(data, {allowHtml:true});\n" +
                "      }\n" +
                "   </script>\n" +
                "    </head>\n" +
                "  <body>\n" +
                "    <div id=\"chart_div\"></div>\n" +
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
