package com.example.enzo

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.AllChatsAdapter
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.Adapters.SearchResultRVAdapter
import com.example.enzo.Fragments.HomeFrag
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.AllChatsModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.miguelcatalan.materialsearchview.MaterialSearchView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class SearchResult : AppCompatActivity() {
    lateinit  var searchRecyclerView: RecyclerView
    lateinit var searchResultList: ArrayList<AdModel>
    lateinit var fStore:FirebaseFirestore
    lateinit var searchResultAdapter:SearchResultRVAdapter
    lateinit var mtSearchView:SearchView
    lateinit var homeFrag:HomeFrag
    lateinit var list:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
       this.supportActionBar?.hide()

///initializing
        mtSearchView=findViewById(R.id.mtSearchView)
        list= arrayListOf()
        fStore= FirebaseFirestore.getInstance()
        searchRecyclerView= findViewById(R.id.searchRecyclerView)
        searchResultList= arrayListOf<AdModel>()
////focusing searchView on Activity Start
         mtSearchView.setFocusable(true)
        mtSearchView.setIconified(false)




///recycler view adapter setting
        searchRecyclerView.layoutManager= LinearLayoutManager(this)
        searchRecyclerView.setHasFixedSize(true)

        searchResultAdapter= SearchResultRVAdapter(this, searchResultList)
        searchRecyclerView.adapter=searchResultAdapter

        searchResultList.clear()


///////suggestions adapter
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(this, R.layout.search_sugges_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        val suggestions = listOf("   Netflix Account", "   Gaming Account", "   Fiver Account", "   Instagram Account", "   Facebook Page", "   Influencer")

        mtSearchView.suggestionsAdapter = cursorAdapter

///////search suggestions listener
        mtSearchView.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {

                val cursor = mtSearchView.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                mtSearchView.setQuery(selection, false)

                // Do something with selection
                return true
            }
        })

//////searchView text submit listener
        mtSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchResultList.clear()
                searchResultAdapter.notifyDataSetChanged()
                mtSearchView.clearFocus()
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
                                var allImagesUrl:String= qds.getString("adAllImages").toString()

                              list.add(displayAdSearchTitle)
                               if (displayAdSearchTitle.contains(query!!.trim().toLowerCase())){

                                searchResultList.add(AdModel(adTitle = displayAdTitle ,
                                    adDetail = displayAdDetail,
                                    adPrice = displayAdPrice,
                                    adImageUrl = displayAdImage,
                                    adType = displayAdType,
                                    adUserId = displayAdUserId,
                                    adSearchTitle = displayAdSearchTitle,
                                adAllImages = allImagesUrl,
                                null,
                                null))

                               searchResultAdapter.notifyDataSetChanged()
                               }





                            }
/////if search word not exsist in account  names(doing it outside loop)

                            if (!list.contains(query!!.trim().toLowerCase())){

                                Toast.makeText(this@SearchResult, "No search results found", Toast.LENGTH_SHORT).show()

                            }

                        }

                    })


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }

                cursorAdapter.changeCursor(cursor)
                return true

            }
        })

    }


////onBack pressed
    override fun onBackPressed() {

        if (mtSearchView.isFocused){
            mtSearchView.clearFocus()
        }else{
               val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_out, R.anim.activity_fade_in)
        }
}
}