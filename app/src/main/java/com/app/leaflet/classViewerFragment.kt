package com.app.leaflet

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var univClassRecyclerAdapter: UnivClassRecyclerAdapter
private lateinit var recyclerViewClass: RecyclerView


class classViewerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_class_viewer, container, false)

        // Configurer le Spinner
        setupSpinner(view)
        setupAddClassBtn(view)

        recyclerViewClass = view?.findViewById(R.id.recyclerViewClass)!!

        univClassRecyclerAdapter = UnivClassRecyclerAdapter()

        recyclerViewClass.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = univClassRecyclerAdapter
        }

        univClassRecyclerAdapter.updateList(emptyList())

        return view
    }

     private fun setupAddClassBtn(view: View){
         val btn:Button = view.findViewById(R.id.addClassBtn)

         btn.setOnClickListener{
             val intent = Intent(requireContext(), NewClass::class.java)
             startActivity(intent)
         }
    }

    private fun setupSpinner(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner)

        val database = LeafLetLocalDatabase.getDatabase(requireContext())
        val univClassDao = database.univClassDao()


        // Préparer les données pour le Spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = 2015

        val items = (startYear..currentYear + 3).map { year ->
            "$year-${year + 1}"
        }


        // Créer et configurer l'adapter
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            items
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        // Associer l'adapter au Spinner
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]

                lifecycleScope.launch(Dispatchers.IO) {
                    val fetchedClasses = univClassDao.getClassByYear(selectedItem)

                    withContext(Dispatchers.Main) {
                        univClassRecyclerAdapter.updateList(fetchedClasses)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aucune action si aucun élément n'est sélectionné
            }
        }

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            classViewerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}