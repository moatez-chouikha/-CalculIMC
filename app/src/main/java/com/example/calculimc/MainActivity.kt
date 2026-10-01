package com.example.calculimc

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoids: EditText
    private lateinit var editTextTaille: EditText
    private lateinit var buttonCalculer: Button
    private lateinit var buttonEffacer: Button
    private lateinit var textViewImc: TextView
    private lateinit var textViewCategorie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextPoids = findViewById(R.id.editTextPoids)
        editTextTaille = findViewById(R.id.editTextTaille)
        buttonCalculer = findViewById(R.id.buttonCalculer)
        buttonEffacer = findViewById(R.id.buttonEffacer)
        textViewImc = findViewById(R.id.textViewImc)
        textViewCategorie = findViewById(R.id.textViewCategorie)

        buttonCalculer.setOnClickListener {
            hideKeyboard()
            calculerIMC()
        }

        buttonEffacer.setOnClickListener {
            effacerChamps()
        }
    }

    private fun calculerIMC() {
        val poidsStr = editTextPoids.text.toString().trim()
        val tailleStr = editTextTaille.text.toString().trim()

        if (poidsStr.isEmpty() || tailleStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val poids = poidsStr.replace(',', '.').toDoubleOrNull()
        var taille = tailleStr.replace(',', '.').toDoubleOrNull()

        if (poids == null || taille == null || poids <= 0 || taille <= 0) {
            Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        // Si l'utilisateur saisit la taille en centimètres (ex: 175 au lieu de 1.75), on convertit automatiquement en mètres
        if (taille > 3.0) {
            taille /= 100.0
        }

        val imc = poids / (taille * taille)
        val formattedImc = String.format(Locale.getDefault(), "%.2f", imc)

        textViewImc.text = getString(R.string.result_imc_format, formattedImc)

        val (categoryResId, colorResId) = when {
            imc < 18.5 -> Pair(R.string.cat_insuffisance_ponderale, R.color.colorOrange)
            imc < 25.0 -> Pair(R.string.cat_corpulence_normale, R.color.colorGreen)
            imc < 30.0 -> Pair(R.string.cat_surpoids, R.color.colorOrange)
            imc < 35.0 -> Pair(R.string.cat_obesite_moderee, R.color.colorRed)
            imc < 40.0 -> Pair(R.string.cat_obesite_severe, R.color.colorRed)
            else -> Pair(R.string.cat_obesite_morbide, R.color.colorDarkRed)
        }

        textViewCategorie.setText(categoryResId)
        textViewCategorie.setTextColor(ContextCompat.getColor(this, colorResId))
    }

    private fun effacerChamps() {
        editTextPoids.text?.clear()
        editTextTaille.text?.clear()
        textViewImc.text = ""
        textViewCategorie.text = ""
        editTextPoids.requestFocus()
    }

    private fun hideKeyboard() {
        val currentFocusView = currentFocus ?: findViewById(R.id.main)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
    }
}