package edu.piedpiper.uw.ischool.watsonchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView

class PersonalityPersonality : AppCompatActivity(), GestureDetector.OnDoubleTapListener {
    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    private lateinit var wv : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personality)

        wv = findViewById(R.id.web)

        val data = intent.extras.getSerializable("data")
                as HashMap<HashMap<String, ArrayList<Double>>, HashMap<String, ArrayList<Double>>>

        //val keys : HashMap<String, ArrayList<Double>> = ArrayList(data.keys)
        var topLevel : String = ""
        var nested : String = ""

        data.keys.forEach { item ->
            val child = data.getValue(item)
            val value = ArrayList<String>(item.keys)[0]
            val percentile = item.getValue(value)[0]
            val raw = item.getValue(value)[1]

            topLevel += "['" + value + "',   'Big 5 Traits', " + percentile + ", " + raw + "],"

            child.forEach { obj ->
                val name = obj.key
                val r = obj.value[0]
                val p = obj.value[1]

                nested += "['" + name + "', '" + value + "', " + p + ", " + r + "],"
            }
        }

        val chart : String = "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "           google.charts.load('current', {\n" +
                "        'packages': ['treemap']\n" +
                "      });\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      function drawChart() {\n" +
                "\n" +
                "\n" +
                "        var firstClick = 0;\n" +
                "        var secondClick = 0;\n" +
                "\n" +
                "        var data = google.visualization.arrayToDataTable([\n" +
                "          ['Characteristic', 'Parent', 'Percentile', 'Score'],\n" +
                "          ['Big 5 Traits', null, 0, 0],\n" + topLevel + nested +
                "        ]);\n" +
                "\n" +
                "        tree = new google.visualization.TreeMap(document.getElementById('chart_div'));\n" +
                "\n" +
                "        tree.draw(data, {\n" +
                "          minColor: '#009688', midColor: '#f7f7f7', maxColor: '#ee8100'," +
                "          headerHeight: 15,\n" +
                "          fontColor: 'black',\n" +
                "          showScale: true\n" +
                "        });\n" +
                "\n" +
                "        google.visualization.events.addListener(tree, 'select', function() {\n" +
                "          var date = new Date();\n" +
                "          var millis = date.getTime();\n" +
                "\n" +
                "          if (millis - secondClick > 1000) {\n" +
                "            setTimeout(function() {\n" +
                "            }, 250);\n" +
                "          }\n" +
                "\n" +
                "          if (millis - firstClick < 250) {\n" +
                "            firstClick = 0;\n" +
                "            secondClick = millis;\n" +
                "\n" +
                "            tree.goUpAndDraw()\n" +
                "\n" +
                "          } else {\n" +
                "            firstClick = millis;\n" +
                "            secondClick = 0;\n" +
                "          }\n" +
                "        });\n" +
                "\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div>\n" +
                "  </body>\n" +
                "</html>"

        val settings : WebSettings = wv.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        //settings.builtInZoomControls = true

        wv.loadDataWithBaseURL("file:///android_asset/", chart, "text/html", "utf-8", null )
    }
}
