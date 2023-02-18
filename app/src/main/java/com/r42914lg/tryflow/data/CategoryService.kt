package com.r42914lg.tryflow.data

import com.r42914lg.tryflow.domain.Category
import com.r42914lg.tryflow.domain.CategoryDetailed
import com.r42914lg.tryflow.domain.Clue
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

interface CategoryService {

    @GET("categories") suspend fun getCategories(
        @Query("count") amountOfCluesToReturn: Int,
        @Query("offset") offset: Int,
    ): List<Category>

    @GET("category")
    suspend fun getDetailedCategory(@Query("id") categoryId: Int): CategoryDetailed
}

class CategoryServiceTestImpl @Inject constructor(): CategoryService {

    private val catList = listOf(
        Category(1, "Category 1", 3),
        Category(2, "Category 2", 4),
        Category(3, "Category 3", 5),
        Category(4, "Category 4", 2),

    )

    private val detailsList = listOf(
        CategoryDetailed(1, "Category 1", 3, listOf(
                Clue(11, "answer 11", "question 11"),
                Clue(12, "answer 12", "question 12"),
                Clue(13, "answer 13", "question 13")
            )
        ),
        CategoryDetailed(2, "Category 2", 4, listOf(
                Clue(21, "answer 21", "question 21"),
                Clue(22, "answer 22", "question 22"),
                Clue(23, "answer 23", "question 23"),
                Clue(24, "answer 24", "question 24"),
            )
        ),
        CategoryDetailed(3, "Category 3", 5, listOf(
                Clue(31, "answer 31", "question 31"),
                Clue(32, "answer 32", "question 32"),
                Clue(33, "answer 33", "question 33"),
                Clue(34, "answer 34", "question 34"),
                Clue(35, "answer 35", "question 35"),
            )
        ),
        CategoryDetailed(4, "Category 4", 2, listOf(
                Clue(41, "answer 41", "question 41"),
                Clue(42, "answer 42", "question 42"),
            )
        ),
    )

    override suspend fun getCategories(amountOfCluesToReturn: Int, offset: Int): List<Category> {
        return catList
    }

    override suspend fun getDetailedCategory(categoryId: Int): CategoryDetailed {
        return detailsList.find { it.id == categoryId }
            ?: throw IllegalArgumentException("No details with ID = $categoryId")
    }

}

