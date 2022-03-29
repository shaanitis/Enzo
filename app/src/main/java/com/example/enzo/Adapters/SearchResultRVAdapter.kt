package com.example.enzo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Fragments.ViewCategoryAds
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.SearchAdOnClick
import com.example.enzo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class SearchResultRVAdapter(
    var context: Context,
    private var searchResultList: ArrayList<AdModel>,
    var adIds: ArrayList<String>,
    val onClick: SearchAdOnClick
): RecyclerView.Adapter<SearchResultRVAdapter.MyViewHolder>()  {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv_item, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= searchResultList.get(position)

        Picasso.get().load(currentItem.adImageUrl).placeholder(R.drawable.gray).into(holder.adImage)
        holder.adTitle.text= currentItem.adTitle
        holder.adPrice.text= "Rs ${currentItem.adPrice}"

       /* to get on click listener in activity we created a seperate OnClick interface class
       and created a function onItemClick in it
       then passed argument position:Int to get position of item clicked in this function's constructor
       then in adapter constructor we added object of this OnClick class as argument
       whose value that we passed from activity of this recycler view to adapter constructor was = this,
        means pass 'this' both in fragment and activity
        then as shown below on the holder onclicklistener we called that callback object and the onItemCLick function
        that was in that OnClick interface and pass position argument by position provided by adapter on item click
        then we implement or : that OnClick interface in the activity of recycler view
        after implementing we will call the onItemClick function of that interface in Main of the activity
        then in that function we will get the position of item click and can do anything we want like go to next activity
        and to send data of recycler view item as extra
        we can send it with arraylist of adapter + position+ any variable of arraylist<Model> that we want like this
        intent.putExtra("adViewImage", searchResultList[pos].adImageUrl)
        AND we can also pass image and text views on which we want to apply shared element transition as arguments
        in onItemCLick fun of OnClick interface
       * */
        holder.clickListenerView?.setOnClickListener {
            onClick!!.onAdItemClick(pos = position,holder.adImage)

        }

        holder.saveAdBtn.setOnClickListener {
            Toast.makeText(context, "Ad saved to your list", Toast.LENGTH_SHORT).show()
            holder.saveAdBtn.setImageResource(R.drawable.hearticon)
          val strRef:DocumentReference=  fStore.collection("savedAds").document(adIds[position])
            val hash= hashMapOf("userId" to auth.currentUser?.uid.toString())
            strRef.set(hash)
        }

        //////on click listener used and ad details sent to next activity//////////
        /*holder.clickListenerView?.setOnClickListener {

            val intent= Intent(context, ChattingScreen::class.java)
            intent.putExtra("idOfUploaderChatting", currentItem.idOfUserChatClicked)
            context.startActivity(intent)
        }
*/



    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val adImage: ImageView =itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val saveAdBtn:ImageButton= itemView.findViewById(R.id.saveAdBtn)
        val clickListenerView: View?= itemView



    }
//////if unable to detect and assign layouts or views, remove the import library .R////////

}