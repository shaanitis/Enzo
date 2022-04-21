package com.example.enzo

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.viewpager.widget.ViewPager
import com.example.enzo.Adapters.WelcomeAdapter

class WelcomeScreen : AppCompatActivity() {

    /////////Declaring elements of view pager adapter in main/////
    lateinit var myAdapter:WelcomeAdapter
    lateinit var viewPager: ViewPager
    lateinit var dotsTv: Array<TextView?>
    lateinit var pageDotsLayout: LinearLayout
    lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        this.supportActionBar?.hide()


        ///////////////////////////check if app is not started first time///////
        val sp: SharedPreferences = getSharedPreferences("firstTimeCheck", MODE_PRIVATE)
        if (sp.contains("isFirstTime")){
            if (sp.getBoolean("isFirstTime", true)==false){
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()

            }
        }



        //////////////////////////// declaring visual buttons and layouts//////


        viewPager= findViewById(R.id.viewPager)

        val skip: AppCompatButton = findViewById(R.id.back)
        val next: AppCompatButton= findViewById(R.id.next)
        pageDotsLayout= findViewById(R.id.pageDots)

////////////view pager adapter implementation//////////////////

        layouts= intArrayOf(R.layout.slider1, R.layout.slider2, R.layout.slider3, R.layout.slider4)
        myAdapter= WelcomeAdapter(this, layouts)
        viewPager.adapter=myAdapter

////////////change listener of VP adapter/////////////
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int)  {
                if(position== layouts.size-1){
                    next.text="Start"
                    skip.visibility= View.GONE
                } else{
                    next.text="Next"
                    skip.visibility= View.VISIBLE
                }
                setDots(position)
            }



            override fun onPageScrollStateChanged(state: Int) {

            }

        }
        )
        ///////////////setting 0 to setDots method that generated dotes for pages///////////
        setDots(0)

        /////skip btn including first time app run check//////////

        skip.setOnClickListener {


            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()

            val sp: SharedPreferences = getSharedPreferences("firstTimeCheck", MODE_PRIVATE)
            val ed: SharedPreferences.Editor= sp.edit()
            ed.putBoolean("isFirstTime", false)
            ed.commit()
        }
        /////next page btn including first time app run check and settings to view next pages//////////

        next.setOnClickListener {


            val currentPage: Int= viewPager.currentItem
            if(currentPage== 3){
                val i = Intent(this, LoginActivity::class.java)
                val p2: Pair<View, String>
                p2= Pair(next, "start")

                val extra= ActivityOptionsCompat.makeSceneTransitionAnimation(this, p2)
                startActivity(i, extra.toBundle())
                finish()

                //////////////////////// checkFirstTime//////////
                val sp: SharedPreferences = getSharedPreferences("firstTimeCheck", MODE_PRIVATE)
                val ed: SharedPreferences.Editor= sp.edit()
                ed.putBoolean("isFirstTime", false)
                ed.commit()

                ////////////
            }
            else if( currentPage<layouts.size){


                viewPager.currentItem=currentPage+1

            }
            else
            {

                val i = Intent(this, LoginActivity::class.java)
                val p2: Pair<View, String>
                p2= Pair(next, "start")

                val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(this, p2)
                startActivity(i, extras.toBundle())
                finish()
            }


        }

    }

    /////////////settings dots for pages by html generation method/////////
    fun setDots( position: Int){
        dotsTv= arrayOfNulls(layouts.size)
        pageDotsLayout.removeAllViews()
        for (i in dotsTv.indices){

            dotsTv[i] = TextView(this);
            dotsTv[i]?.setText(Html.fromHtml("&#8226"));
            dotsTv[i]?.setTextSize(35F);
            dotsTv[i]?.setTextColor(getResources().getColor(R.color.orange_00))
            pageDotsLayout.addView(dotsTv[i]);
        }
        dotsTv[position]?.setTextColor(getResources().getColor(R.color.orange_main))
    }


}