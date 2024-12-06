package com.app.leaflet

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var univClassRecyclerAdapter: UnivClassRecyclerAdapter
private lateinit var recyclerViewClass: RecyclerView

private lateinit var univGroupRecyclerAdapter: UnivGroupRecyclerAdapter
private lateinit var recyclerViewGroup: RecyclerView

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
        val view2 = inflater.inflate(R.layout.item_class, container, false)

        // Configurer le Spinner
        setupSpinner(view)

        recyclerViewClass = view?.findViewById(R.id.recyclerViewClass)!!
        recyclerViewGroup = view2.findViewById(R.id.recyclerViewGroup)!!

        univClassRecyclerAdapter = UnivClassRecyclerAdapter()
        univGroupRecyclerAdapter = UnivGroupRecyclerAdapter()

        recyclerViewClass.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = univClassRecyclerAdapter
        }

        recyclerViewGroup.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = univGroupRecyclerAdapter
        }

        // Ajouter des données fictives au RecyclerView
        val classes = mutableListOf(
            UnivClass(1345543, "Mathématiques", "Salle A1", "Prof. Dupont", 2024),
            UnivClass(1345544, "Physique", "Salle B2", "Prof. Martin", 2023),
            UnivClass(1345545, "Chimie", "Salle C3", "Prof. Durand", 2022)
        )

        univClassRecyclerAdapter.updateList(classes)

        // Ajouter des données fictives au RecyclerView
        val groupes = mutableListOf(
            UnivGroup(13455432, "group 1", "TD", 2024,1345543),
            UnivGroup(13455442, "group 2", "TD",  2023,1345544),
            UnivGroup(13455452, "group 3", "TP", 2022,1345545)
        )

        univGroupRecyclerAdapter.updateList(groupes)

        return view
    }

    private fun setupSpinner(view: View?) {
        // Récupérer le Spinner
        val spinner: Spinner = view?.findViewById(R.id.spinner)!!

        // Préparer les données pour le Spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = 2015

        val items = (startYear..currentYear).map { year ->
            "$year/${year + 1}"
        }


        // Créer et configurer l'adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Associer l'adapter au Spinner
        spinner.adapter = adapter

        // Gérer les sélections de l'utilisateur
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
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