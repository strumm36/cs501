package com.example.recipebrowser

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.Date

data class Recipe(
    val title: String,
    val ingredients: String,
    val steps: String
)

class RecipeViewModel : ViewModel() {
    var listItems = mutableStateListOf<Recipe>()

    fun addRecipe (title: String, ingredients: String, steps: String) {
        listItems.add(Recipe(title, ingredients, steps))
    }

    init {
        // Add 5 sample recipes (AI-Generated)
        listItems.addAll(
            listOf(
                Recipe(
                    title = "Spaghetti Carbonara",
                    ingredients = """
                        200g spaghetti
                        100g pancetta
                        2 large eggs
                        50g parmesan cheese
                        Salt & pepper
                    """.trimIndent(),
                    steps = """
                        1. Boil pasta in salted water.
                        2. Fry pancetta until crispy.
                        3. Beat eggs with cheese in a bowl.
                        4. Drain pasta and mix with pancetta.
                        5. Remove from heat, add egg mixture, and stir quickly.
                    """.trimIndent()
                ),
                Recipe(
                    title = "Classic Pancakes",
                    ingredients = """
                        1 cup flour
                        2 tbsp sugar
                        1 cup milk
                        1 egg
                        1 tsp baking powder
                    """.trimIndent(),
                    steps = """
                        1. Mix dry ingredients in a bowl.
                        2. Whisk in milk and egg until smooth.
                        3. Heat a pan and pour batter.
                        4. Cook until bubbles form, then flip.
                        5. Serve with syrup or fruit.
                    """.trimIndent()
                ),
                Recipe(
                    title = "Guacamole",
                    ingredients = """
                        2 ripe avocados
                        1 lime, juiced
                        1 tomato, diced
                        1/2 onion, chopped
                        Salt to taste
                    """.trimIndent(),
                    steps = """
                        1. Mash avocados in a bowl.
                        2. Add lime juice, tomato, onion, and salt.
                        3. Mix well and serve fresh.
                    """.trimIndent()
                ),
                Recipe(
                    title = "Grilled Cheese Sandwich",
                    ingredients = """
                        2 slices of bread
                        2 slices of cheese
                        Butter for spreading
                    """.trimIndent(),
                    steps = """
                        1. Butter one side of each bread slice.
                        2. Place cheese between unbuttered sides.
                        3. Grill on a pan until golden brown on both sides.
                    """.trimIndent()
                ),
                Recipe(
                    title = "Chicken Stir-Fry",
                    ingredients = """
                        1 chicken breast, sliced
                        1 cup mixed vegetables
                        2 tbsp soy sauce
                        1 tbsp olive oil
                        1 garlic clove, minced
                    """.trimIndent(),
                    steps = """
                        1. Heat oil in a pan.
                        2. Add garlic and sauté for 30 seconds.
                        3. Add chicken and cook until golden.
                        4. Add vegetables and soy sauce.
                        5. Stir-fry for 5 minutes and serve hot.
                    """.trimIndent()
                )
            )
        )
    }
}