package com.example.enzo.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.enzo.Adapters.CategoryGridViewAdapter
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.enzo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception


class SelectCategoryFrag : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



         val view: View = inflater.inflate(R.layout.fragment_select_category, container, false)

        try {
         val navBar: BottomNavigationView = requireActivity().findViewById(R.id.navBar)
         navBar.visibility = View.VISIBLE
         val gridView: GridView = view.findViewById(R.id.selectCategoryGridView)
         val categoryNames: Array<String>
         val categoryIcons: Array<Int>
         categoryNames = arrayOf(
             "Freelancing Account",
             "Gaming Account",
             "Instagram Account",
             "Facebook Group/Page",
             "Youtube Channel",
             "Influencer Account",
             "Other Accounts"
         )
         val categories: Array<String>
         categories = arrayOf(
             "freelancing",
             "gaming",
             "instagram",
             "facebook",
             "youtube",
             "influencer",
             "other"
         )
         categoryIcons = arrayOf(
             R.drawable.categoryfreelance,
             R.drawable.categorygaming,
             R.drawable.categoryinstagram,
             R.drawable.categoryfacebook,
             R.drawable.categoryyoutube,
             R.drawable.categoryinfluencer,
             R.drawable.categoryother
         )

         val categoryGridViewAdapter: CategoryGridViewAdapter = CategoryGridViewAdapter(
             requireContext(),
             categoryNames = categoryNames,
             categorIcons = categoryIcons
         )
         gridView.adapter = categoryGridViewAdapter

         gridView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
             override fun onItemClick(
                 parent: AdapterView<*>?,
                 view: View?,
                 position: Int,
                 id: Long
             ) {

                 MaterialAlertDialogBuilder(requireContext())
                     .setTitle("Continue creating Ad?")
                     .setMessage("You have selected " + categoryNames[position] + " category")
                     .setNegativeButton("No") { dialog, it ->
                         Log.d("", "")
                     }
                     .setPositiveButton("Yes, continue") { dialog, it ->

                         findNavController().navigate(
                             R.id.action_adCategory_to_addFrag,
                             Bundle().apply {
                                 putString("adCategory", categories[position])
                             })
                     }.show()
//////navigating by nav controller


/////navigating manually
                 /*   val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().apply {
                    replace(R.id.frameLayout, fragment)
                    addToBackStack("")
                    commit()  }*/
                 /////manually transferring data
                 /*val bundle=Bundle()
                bundle.putString("adCategory", categories[position])
                val fragment: AddFrag= AddFrag()
                fragment.arguments=bundle*/

             }

         })
//////navigating back to home frag on back pressed
         val callback = object : OnBackPressedCallback(true) {
             override fun handleOnBackPressed() {
                 findNavController().navigate(R.id.action_adCategory_to_homeFrag)
             }

         }
         requireActivity().onBackPressedDispatcher.addCallback(callback)



     }catch (e:Exception){
         Log.d("","")
     }
        return view
    }



}