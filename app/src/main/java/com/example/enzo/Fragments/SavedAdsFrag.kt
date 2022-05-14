package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.enzo.Adapters.SavedAdsAdapter
import com.example.enzo.MainActivity
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.SavedAdsOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.example.enzo.ViewModels.SavedAdsVM
import com.example.enzo.databinding.FragmentNotifyBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class SavedAdsFrag : Fragment(), SavedAdsOnClick {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var savedAdList:ArrayList<AdModel>
    lateinit var savedAdIds:ArrayList<String>
    lateinit var adIds:ArrayList<String>
    lateinit var savedAdsRVAdapter:SavedAdsAdapter
    lateinit var viewModel:SavedAdsVM
    lateinit var bind:FragmentNotifyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentNotifyBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SavedAdsVM::class.java]

        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        savedAdList= arrayListOf()
        savedAdIds= arrayListOf()
        adIds= arrayListOf()




       bind.savedAdsRV.layoutManager= LinearLayoutManager(requireContext())
        bind.savedAdsRV.setHasFixedSize(true)


        savedAdsRVAdapter= SavedAdsAdapter(requireContext(), savedAdList, this)
       bind. savedAdsRV.adapter=savedAdsRVAdapter

////////getting Saved Ads through ViewModel

        bind.strSavedAds.setOnRefreshListener {

               savedAdList.clear()
           Log.d("viewModel", "repoFirstSavedAds")
           fStore.collection("savedAds")
               .whereEqualTo("userId", auth.currentUser?.uid.toString())
               .get()
               .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                   override fun onSuccess(querySnapshot: QuerySnapshot?) {

       for (it: QueryDocumentSnapshot in querySnapshot!!) {
           val adId: String = it.id.toString()



           savedAdIds.add(adId)


       }
       for (i in 0 until savedAdIds.size) {
           fStore.collection("ads").document(savedAdIds[i]).get().addOnSuccessListener {


               val adId: String = it.id.toString()
               val displayAdTitle: String = it.getString("adTitle").toString()
               var displayAdPrice: String = it.getString("adPrice").toString()
               var displayAdImage: String = it.getString("adImageUrl").toString()
               var displayAdDetail: String = it.getString("adDetail").toString()
               var displayAdType: String = it.getString("adType").toString()
               var displayAdUserId: String = it.getString("adUserId").toString()
               var displayAdSearchTitle: String = it.getString("adSearchTitle").toString()
               var allImagesUrl: String = it.getString("adAllImages").toString()
               var adPhoneNo: String = it.getString("adPhoneNo").toString()
               var adLocation: String = it.getString("adLocation").toString()


               savedAdList.add(
                   AdModel(
                       adTitle = displayAdTitle,
                       adDetail = displayAdDetail,
                       adPrice = displayAdPrice,
                       adImageUrl = displayAdImage,
                       adType = displayAdType,
                       adUserId = displayAdUserId,
                       adSearchTitle = displayAdSearchTitle,
                       adAllImages = allImagesUrl,
                       adPhoneNo = adPhoneNo,
                       null,null, adId
                   )
               )
               bind.savedAdsRV.startLayoutAnimation()
savedAdsRVAdapter.notifyDataSetChanged()


           }.addOnFailureListener {
               Log.e("hh", "")
           }

       }

                       savedAdsRVAdapter.notifyDataSetChanged()

                   }

               })
            bind.strSavedAds.isRefreshing=false
        }

       viewModel.getSavedAds().observe(this, Observer {
           savedAdList.clear()
           savedAdList.addAll(it)
           if (savedAdList.isEmpty()) {

               bind.shimmerSavedAds.visibility=View.GONE

           }else{
               bind.savedAdsRV.startLayoutAnimation()
               bind.shimmerSavedAds.visibility=View.GONE
               savedAdsRVAdapter.notifyDataSetChanged()
           }
       })


/////on swipe to refresh



        bind.strSavedAds.setColorSchemeResources(R.color.orange_200)

        viewModel.refreshing().observe(this, Observer {
            bind.strSavedAds.isRefreshing=it
        })
//////deleting on swipe
        deleteDialogOnItemSwipe()





        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_notifyFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)



    }






    private fun deleteDialogOnItemSwipe() {
        val swipeGesture= object : SwipeGestures(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {

                    ItemTouchHelper.LEFT -> {

                        MaterialAlertDialogBuilder(requireContext()).setTitle("Want to remove Ad from your list?")
                            .setNegativeButton("No"){dialog, it->
                                savedAdsRVAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete"){dialog, it->

                                savedAdsRVAdapter.deleteItem(viewHolder.position)
                                val sb= Snackbar.make(bind.savedAdsRV, "Ad removed from your list", Snackbar.LENGTH_SHORT)
                                sb.setAction("Got It"){
                                    sb.dismiss()
                                }.show()
                            }
                            .show()

                    }
                }

            }
        }

        val touchHelper= ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(bind.savedAdsRV)


    }


    override fun onAdItemClick(pos: Int, adImage: ImageView) {
        val intent= Intent(requireContext(), ViewAdActivity::class.java)
        intent.putExtra("adViewImage", savedAdList[pos].adImageUrl)
        intent.putExtra("adViewTitle", savedAdList[pos].adTitle)
        intent.putExtra("adViewPrice", savedAdList[pos].adPrice)
        intent.putExtra("adViewDetail", savedAdList[pos].adDetail)
        intent.putExtra("idOfUploader", savedAdList[pos].adUserId)
        intent.putExtra("adAllImages", savedAdList[pos].adAllImages)
        intent.putExtra("adId", savedAdList[pos].adId)


        val p2: Pair<View, String>
        p2= Pair(adImage, "viewAdImgTrans")

        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos, adImage)
    }





}


