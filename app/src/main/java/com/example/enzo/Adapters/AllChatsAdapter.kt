package com.example.enzo.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.OnClickRV.UserChatOnClick
import com.example.enzo.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class AllChatsAdapter(
    var context: Context,
    private var allChatsList: ArrayList<AllChatsModel>,
    val onCLick: UserChatOnClick?
): RecyclerView.Adapter<AllChatsAdapter.MyViewHolder>()  {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()
    val db:FirebaseDatabase= FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_all_chats_rv, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= allChatsList.get(position)

        Picasso.get().load(currentItem.imgOfUserChatClicked).placeholder(R.drawable.blankuser).into(holder.image)
        holder.name.text= currentItem.nameOfUserChatClicked



        //////on click listener used and ad details sent to next activity//////////
        holder.clickListenerView?.setOnClickListener {
            onCLick?.onAdItemClick(pos = position, userName = holder.name, userImg = holder.image)
        }




    }

    override fun getItemCount(): Int {
        return allChatsList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: RoundedImageView=itemView.findViewById(R.id.friendImg)
        val name: TextView = itemView.findViewById(R.id.name)
        val clickListenerView: View?= itemView

    }


    fun deleteItem(i:Int){


        val currentItem = allChatsList.get(i)
        allChatsList.removeAt(i)
        notifyDataSetChanged()

              fStore.collection("users").document(auth.currentUser?.uid.toString())
                  .collection("idOfUploaderChats").document(currentItem.idOfUserChatClicked).delete()




///removing all docs from a collection


             fStore.collection("chats").document(auth.currentUser?.uid.toString())
                 .collection(auth.currentUser?.uid.toString() + currentItem.idOfUserChatClicked)
                 .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                     override fun onSuccess(qs: QuerySnapshot?) {
                         for (qds: QueryDocumentSnapshot in qs!!) {
                             val id = qds.id
                             fStore.collection("chats").document(auth.currentUser?.uid.toString())
                                 .collection(auth.currentUser?.uid.toString() + currentItem.idOfUserChatClicked)
                                 .document(id).delete()

                         }



                     }

                 })


         }



//////if unable to detect and assign layouts or views, remove the import library .R////////

}