package com.example.progetto2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home.*
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.progetto2.datamodel.flag
import com.google.android.gms.flags.Flag


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Home.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        ps4Button.setOnClickListener {
            flag = 1
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        }
        xboxButton.setOnClickListener {
            flag = 2
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#107C10")))
        }
        nintendoButton.setOnClickListener {
            flag = 3
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#DD0001")))
        }


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
