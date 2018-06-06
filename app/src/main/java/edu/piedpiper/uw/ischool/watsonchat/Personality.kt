package edu.piedpiper.uw.ischool.watsonchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class Personality : AppCompatActivity() {

    private val options = arrayListOf("Personal Needs Predictions", "Consumption Predictions", "Personality Insights",
            "Predicted Psychological Values")
    private lateinit var lv: ListView
    private lateinit var words : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personality)

        lv = findViewById(R.id.list)
        words = findViewById(R.id.words)

        val list = ArrayList<String>(options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lv.adapter = adapter

        val wordCount : Long = intent.extras.getLong("words")
        words.text = "Total Words Analyzed: " + wordCount

        val consumption = intent.extras.getSerializable("consumption") as HashMap<String, Map<String, Double>>
        val values = intent.extras.getSerializable("values") as HashMap<String, ArrayList<Double>>
        val needs = intent.extras.getSerializable("needs") as  HashMap<String, ArrayList<Double>>
        val personality = intent.extras.getSerializable("personality")
                as HashMap<HashMap<String, ArrayList<Double>>, HashMap<String, ArrayList<Double>>>

        println("VALUES" + values)
        /*lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            when(position){
                0 -> {
                    val intent = Intent(this, PersonalityNeeds::class.java)
                    intent.putExtra("topic", list[position])

                    val bundle : Bundle = Bundle()
                    bundle.putSerializable("data", needs)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

                1 -> {
                    val intent = Intent(this, PersonalityConsumption::class.java)
                    intent.putExtra("topic", list[position])

                    val bundle : Bundle = Bundle()
                    //bundle.putSerializable("data", intent.extras.getSerializable("consumption") as HashMap<String, Map<String, Double>>)
                    bundle.putSerializable("data", consumption)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

                2 -> {
                    val intent = Intent(this, PersonalityPersonality::class.java)
                    intent.putExtra("topic", list[position])

                    val bundle : Bundle = Bundle()
                    //bundle.putSerializable("data", intent.extras.getSerializable("personality") as HashMap<String, HashMap<String, Array<Double>>>)
                    bundle.putSerializable("data", personality)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

                3 -> {
                    val intent = Intent(this, PersonalityValues::class.java)
                    intent.putExtra("topic", list[position])

                    val bundle : Bundle = Bundle()
                    //bundle.putSerializable("data", intent.extras.getSerializable("values") as HashMap<String, Array<Double>>)
                    bundle.putSerializable("data", values)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        } */
    }
}
