package com.example.enzo

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.OnClickRV.SearchAdOnClick
import com.example.enzo.OnClickRV.CustomSearchItemAnimator
import com.example.enzo.Adapters.SearchResultRVAdapter
import com.example.enzo.Fragments.HomeFrag
import com.example.enzo.Models.AdModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import androidx.core.util.Pair

class SearchResult : AppCompatActivity(), SearchAdOnClick {


    lateinit  var searchRecyclerView: RecyclerView
    lateinit var searchResultList: ArrayList<AdModel>
    lateinit var adIds:ArrayList<String>
    lateinit var fStore: FirebaseFirestore
    lateinit var searchResultAdapter: SearchResultRVAdapter
    lateinit var mtSearchView: SearchView
    lateinit var homeFrag: HomeFrag
    lateinit var list:ArrayList<String>
    lateinit var searchNothingImage:ImageView
    lateinit var searchNothingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
               this.supportActionBar?.hide()
        mtSearchView=findViewById(R.id.mtSearchView)
        searchNothingImage=findViewById(R.id.searchNothingImage)
        searchNothingText=findViewById(R.id.searchNothingText)
        searchNothingImage.visibility=View.GONE
        searchNothingText.visibility=View.GONE
        list= arrayListOf()
        adIds= arrayListOf()
        fStore= FirebaseFirestore.getInstance()
        searchRecyclerView=findViewById(R.id.searchRecyclerView)
        searchResultList= arrayListOf<AdModel>()
////focusing searchView on Activity Start
        mtSearchView.setIconified(false)
        mtSearchView.setFocusable(true)

        /////to show keyboard in kotlin fragment
        /* val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
         imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
 */
///recycler view adapter setting

        searchRecyclerView.layoutManager= LinearLayoutManager(this)
        searchRecyclerView.setHasFixedSize(true)

        searchResultAdapter= SearchResultRVAdapter(this, searchResultList, adIds, this)
        searchRecyclerView.adapter=searchResultAdapter
        searchRecyclerView.itemAnimator= CustomSearchItemAnimator()

        searchResultList.clear()

///////suggestions adapter
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(this, R.layout.search_sugges_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        val suggestions = listOf("   Netflix Account", "   Gaming Account", "   Fiver Account", "   Instagram Account", "   Facebook Page", "   Influencer", "   Youtube Channel")

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
//////searchView text cancel listener
        mtSearchView.setOnCloseListener(object : android.widget.SearchView.OnCloseListener,
            SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                mtSearchView.setQuery("", true)
                return true
            }

        })


///////////search view query listener, most imp///////////////
        mtSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchNothingImage.visibility=View.GONE
                searchNothingText.visibility=View.GONE
                searchResultList.clear()
                searchResultAdapter.notifyDataSetChanged()
                mtSearchView.clearFocus()
                fStore.collection("ads")
                    /*.whereEqualTo("adSearchTitle", "${query?.toLowerCase()}")*/
                    .get()
                    .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                        override fun onSuccess(querySnapshot: QuerySnapshot?) {
                            for (qds: QueryDocumentSnapshot in querySnapshot!!){
                                val adId:String= qds.id.toString()
                                val displayAdTitle: String = qds.getString("adTitle").toString()
                                var displayAdPrice: String = qds.getString("adPrice").toString()
                                var displayAdImage:String= qds.getString("adImageUrl").toString()
                                var displayAdDetail:String= qds.getString("adDetail").toString()
                                var displayAdType:String= qds.getString("adType").toString()
                                var displayAdUserId:String= qds.getString("adUserId").toString()
                                var displayAdSearchTitle:String= qds.getString("adSearchTitle").toString()
                                var allImagesUrl:String= qds.getString("adAllImages").toString()
                                var adPhoneNo:String= qds.getString("adPhoneNo").toString()
                                var adLocation:String= qds.getString("adLocation").toString()

                                list.add(displayAdSearchTitle)
                                if (displayAdSearchTitle.contains(query!!.trim().toLowerCase())){
                                    searchNothingImage.visibility=View.GONE
                                    searchNothingText.visibility=View.GONE
                                    searchResultList.add(AdModel(adTitle = displayAdTitle ,
                                        adDetail = displayAdDetail,
                                        adPrice = displayAdPrice,
                                        adImageUrl = displayAdImage,
                                        adType = displayAdType,
                                        adUserId = displayAdUserId,
                                        adSearchTitle = displayAdSearchTitle,
                                        adAllImages = allImagesUrl,
                                        adPhoneNo,
                                        adLocation))

                                    adIds.add(adId)
                                    searchRecyclerView.startLayoutAnimation()
                                    searchResultAdapter.notifyDataSetChanged()
                                }else{
                                    searchNothingImage.visibility = View.VISIBLE
                                    searchNothingText.visibility = View.VISIBLE
                                }





                            }

/////if search word not exsist in account  names(doing it outside loop)

                       /*     if (list.contains(query!!.trim().toLowerCase())) {
                                Log.d("", "")
                            }else{
                                searchNothingImage.visibility = View.VISIBLE
                                searchNothingText.visibility = View.VISIBLE
                            }
*/
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

    override fun onAdItemClick(pos: Int,adImage: ImageView) {


        val intent= Intent(this, ViewAdActivity::class.java)
        intent.putExtra("adViewImage", searchResultList[pos].adImageUrl)
        intent.putExtra("adViewTitle", searchResultList[pos].adTitle)
        intent.putExtra("adViewPrice", searchResultList[pos].adPrice)
        intent.putExtra("adViewDetail", searchResultList[pos].adDetail)
        intent.putExtra("idOfUploader", searchResultList[pos].adUserId)
        intent.putExtra("adAllImages", searchResultList[pos].adAllImages)


        val p2:Pair<View, String>
        p2=Pair(adImage, "viewAdImgTrans")

        val extras=ActivityOptionsCompat.makeSceneTransitionAnimation(this, p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos, adImage)
    }

}
