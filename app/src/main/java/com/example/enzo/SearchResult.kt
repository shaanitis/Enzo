package com.example.enzo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.AllChatsAdapter
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.Adapters.SearchResultRVAdapter
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.AllChatsModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.miguelcatalan.materialsearchview.MaterialSearchView

class SearchResult : AppCompatActivity() {
    lateinit  var searchRecyclerView: RecyclerView
    lateinit var searchResultList: ArrayList<AdModel>
    lateinit var fStore:FirebaseFirestore
    lateinit var searchResultAdapter:SearchResultRVAdapter

    lateinit var toolbar: Toolbar
    lateinit var mtSearchView: MaterialSearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        mtSearchView=findViewById(R.id.mtSearchView)




        fStore= FirebaseFirestore.getInstance()






        searchRecyclerView= findViewById(R.id.searchRecyclerView)
        searchResultList= arrayListOf<AdModel>()


        searchRecyclerView.layoutManager= LinearLayoutManager(this)
        searchRecyclerView.setHasFixedSize(true)


        searchResultAdapter= SearchResultRVAdapter(this, searchResultList)
        searchRecyclerView.adapter=searchResultAdapter

        searchResultList.clear()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_bar_menu, menu)
        val menuItem:MenuItem= menu!!.findItem(R.id.action_search)
        mtSearchView.setMenuItem(menuItem)
        mtSearchView.setEllipsize(true)
        mtSearchView.setSuggestions(resources.getStringArray(R.array.searchSuggestions))
        val searchText= intent.getStringExtra("searchText")
        mtSearchView.setQuery(searchText, true)
        mtSearchView.clearFocus()




        ////on focus, searchView
        mtSearchView.setOnSearchViewListener(object: MaterialSearchView.SearchViewListener{
            override fun onSearchViewShown() {
                searchRecyclerView.visibility=View.GONE
            }

            override fun onSearchViewClosed() {
                searchRecyclerView.visibility=View.VISIBLE
            }

        })

///////on text submit, searchView
        mtSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchResultList.clear()
                searchResultAdapter.notifyDataSetChanged()

                fStore.collection("ads")
                    /*.whereEqualTo("adSearchTitle", "${query?.toLowerCase()}")*/
                    .get()
                    .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                        override fun onSuccess(querySnapshot: QuerySnapshot?) {
                            for (qds: QueryDocumentSnapshot in querySnapshot!!){
                                val displayAdTitle: String = qds.getString("adTitle").toString()
                                var displayAdPrice: String = qds.getString("adPrice").toString()
                                var displayAdImage:String= qds.getString("adImageUrl").toString()
                                var displayAdDetail:String= qds.getString("adDetail").toString()
                                var displayAdType:String= qds.getString("adType").toString()
                                var displayAdUserId:String= qds.getString("adUserId").toString()
                                var displayAdSearchTitle:String= qds.getString("adSearchTitle").toString()

                               if (displayAdSearchTitle.contains(query!!.toLowerCase())){

                                searchResultList.add(AdModel(adTitle = displayAdTitle ,
                                    adDetail = displayAdDetail,
                                    adPrice = displayAdPrice,
                                    adImageUrl = displayAdImage,
                                    adType = displayAdType,
                                    adUserId = displayAdUserId,
                                    adSearchTitle = displayAdSearchTitle))

                               searchResultAdapter.notifyDataSetChanged()}
                               else{
                                       searchResultAdapter.notifyDataSetChanged()
                               }


                            }

                        }

                    })

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (mtSearchView.isSearchOpen){
            mtSearchView.closeSearch()
            searchRecyclerView.visibility=View.VISIBLE
            searchResultAdapter.notifyDataSetChanged()
        }
        super.onBackPressed()
    }
}