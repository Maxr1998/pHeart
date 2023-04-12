package edu.uaux.pheart.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.uaux.pheart.R
import edu.uaux.pheart.preferences.PreferenceKeys.PREF_AGE_DEFAULT_VALUE
import edu.uaux.pheart.preferences.PreferenceKeys.PREF_KEY_AGE
import edu.uaux.pheart.preferences.PreferenceKeys.PREF_KEY_SEX
import edu.uaux.pheart.preferences.PreferenceKeys.PREF_SEX_DEFAULT_VALUE
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * User profile screen.
 */
class ProfileFragment : Fragment(), KoinComponent {

    private val sharedPreferences: SharedPreferences = get()

    private lateinit var ageEditText: TextInputEditText
    private lateinit var biologicalSexText: AutoCompleteTextView
    private lateinit var biologicalSexDropdown: TextInputLayout

    private lateinit var restingHeartRateText: TextView
    private lateinit var exerciseHeartRateText: TextView

    private var age = 0                         // overridden
    private var sex = BiologicalSex.NONE        // overridden

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()

        // re-init dropdown items
        val sexDropdownOptions = listOf(
            DropDownItem(requireContext(), BiologicalSex.MALE),
            DropDownItem(requireContext(), BiologicalSex.FEMALE),
        )
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, sexDropdownOptions)
        biologicalSexText.setAdapter(arrayAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ageEditText = view.findViewById(R.id.age_text)
        biologicalSexText = view.findViewById(R.id.bio_sex_autocomplete_text)
        biologicalSexDropdown = view.findViewById(R.id.bio_sex_dropdown)
        restingHeartRateText = view.findViewById<ConstraintLayout>(R.id.resting_heart_rate).findViewById(R.id.bpm_text)
        exerciseHeartRateText =
            view.findViewById<ConstraintLayout>(R.id.exercise_heart_rate).findViewById(R.id.bpm_text)

        age = sharedPreferences.getInt(PREF_KEY_AGE, PREF_AGE_DEFAULT_VALUE)
        sex = BiologicalSex.fromId(sharedPreferences.getInt(PREF_KEY_SEX, PREF_SEX_DEFAULT_VALUE))
        initialiseInputs()
        updateView()
        registerInputListeners()
    }

    /**
     * Registers listeners for the input fields. React so changes by persisting values and update dependent views.
     */
    private fun registerInputListeners() {
        ageEditText.doOnTextChanged { text, _, _, _ ->
            age = if (text.isNullOrBlank()) PREF_AGE_DEFAULT_VALUE else text.toString().toInt()
            sharedPreferences.edit { putInt(PREF_KEY_AGE, age) }
            updateView()
        }

        biologicalSexText.setOnItemClickListener { adapterView, itemView, position, _ ->
            val selectedItem = adapterView.getItemAtPosition(position) as DropDownItem
            sharedPreferences.edit { putInt(PREF_KEY_SEX, selectedItem.sex.id) }
            sex = selectedItem.sex
            updateView()
        }
    }

    /**
     * Initialises the input fields with the values from the preferences.
     */
    private fun initialiseInputs() {
        ageEditText.setText(if (age == -1) "" else age.toString())
        biologicalSexText.setText(
            when (sex) {
                BiologicalSex.MALE -> requireContext().getString(R.string.option_male)
                BiologicalSex.FEMALE -> requireContext().getString(R.string.option_female)
                else -> ""
            },
        )
    }

    /**
     * Updates the parts of the view dependent on the input fields. The input fields will automatically be up-to-date.
     */
    private fun updateView() {
        if (age == PREF_AGE_DEFAULT_VALUE) {
            restingHeartRateText.text = requireContext().getString(R.string.no_value_indicator)
            exerciseHeartRateText.text = requireContext().getString(R.string.no_value_indicator)
            return
        }

        val restingHeartRate = HeartRateInfo.getRestingHeartRate(age, sex)
        restingHeartRateText.text = requireContext().getString(
            R.string.bpm_range_template,
            restingHeartRate.first,
            restingHeartRate.last,
        )

        val exerciseHeartRate = HeartRateInfo.getExerciseHeartRange(age, sex)
        exerciseHeartRateText.text = requireContext().getString(
            R.string.bpm_range_template,
            exerciseHeartRate.first,
            exerciseHeartRate.last,
        )
    }

    /**
     * Wrapper for drop down menu items.
     */
    private data class DropDownItem(private val context: Context, val sex: BiologicalSex) {
        init {
            require(sex != BiologicalSex.NONE)
        }

        override fun toString() = context.getString(
            when (sex) {
                BiologicalSex.MALE -> R.string.option_male
                BiologicalSex.FEMALE -> R.string.option_female
                else -> error("Invalid enum in when")
            },
        )
    }
}