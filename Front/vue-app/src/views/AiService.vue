<template>
  <div class="container mt-5">
    <div class="card shadow-sm">
      <div class="card-body">
        <form @submit.prevent="fetchRecipes">
          <!-- 요리 방법 -->
          <div class="form-group mb-4">
            <label for="cookingMethod" class="form-label fw-bold">요리 방법</label>
            <select
              id="cookingMethod"
              class="form-select"
              v-model="selectedMethod"
              :disabled="isLoading"
            >
              <option value="">-- 요리 방법 선택 --</option>
              <option v-for="method in cookingMethods" :key="method" :value="method">
                {{ method }}
              </option>
            </select>
          </div>

          <!-- 요리 방식 -->
          <div class="form-group mb-4">
            <label for="cookingCategory" class="form-label fw-bold">요리 방식</label>
            <select
              id="cookingCategory"
              class="form-select"
              v-model="selectedCategory"
              :disabled="isLoading"
            >
              <option value="">-- 요리 카테고리 선택 --</option>
              <option v-for="category in cookingCategories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>

          <!-- 재료 입력 -->
          <div class="form-group mb-4">
            <label for="ingredients" class="form-label fw-bold">재료</label>
            <input
              id="ingredients"
              type="text"
              class="form-control"
              placeholder="재료를 입력하세요 (예: 양파, 당근, 닭고기)"
              v-model="enteredIngredients"
              :disabled="isLoading"
            />
          </div>

          <!-- 검색 버튼 -->
          <div class="text-end mt-4">
            <button class="btn btn-success btn-sm px-3" type="submit" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              {{ isLoading ? '검색 중...' : '검색' }}
            </button>
          </div>
        </form>

        <!-- 로딩 중 표시 -->
        <div v-if="isLoading" class="mt-4 text-center">
          <div class="spinner-border text-success" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2 text-muted">요리 추천 결과를 불러오는 중입니다...</p>
        </div>

        <!-- 결과 출력 -->
        <div v-else-if="recipes.length > 0" class="mt-4">
          <h5>검색 결과:</h5>
          <ul>
            <li v-for="recipe in recipes" :key="recipe.content">
              <!-- '죄송합니다'가 포함된 안내 메시지 처리 -->
              <div v-if="recipe.content.startsWith('죄송합니다')">
                <p class="text-danger" style="white-space: pre-line;">
                  {{ decodeContent(recipe.content) }}
                </p>
              </div>
              <div v-else>
                <strong>방법: {{ recipe.cookingMethod }}</strong> |
                <strong>방식: {{ recipe.cookingCategory }}</strong> |
                <strong>재료: {{ recipe.ingredients }}</strong>
                <p style="white-space: pre-line;">
                  {{ decodeContent(recipe.content) }}
                </p>
              </div>
            </li>
          </ul>
        </div>

        <!-- 검색은 했지만 결과 없음 -->
        <div v-else-if="searched" class="mt-4 text-muted">
          검색 결과가 없습니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      cookingMethods: [
        "굽기", "기타", "끓이기", "데치기", "무침", "볶음", "부침", "비빔",
        "삶기", "절임", "조림", "튀김", "회", "찜", "상관없음"
      ],
      cookingCategories: [
        "간식", "기타", "다이어트", "도시락", "명절", "손님접대", "술안주",
        "야식", "영양식", "이유식", "일상", "초스피드", "해장", "상관없음"
      ],
      selectedMethod: "",
      selectedCategory: "",
      enteredIngredients: "",
      recipes: [],
      searched: false,
      isLoading: false
    };
  },
  methods: {
    decodeContent(content) {
      return content.replace(/\\n/g, '\n').replace(/\\"/g, '"');
    },
    async fetchRecipes() {
      if (!this.selectedMethod || !this.selectedCategory || !this.enteredIngredients) {
        alert("요리 방법, 요리 카테고리, 재료를 모두 입력해주세요.");
        return;
      }

      this.isLoading = true;
      this.recipes = [];
      this.searched = false;

      try {
        const token = localStorage.getItem("jwt");

        const response = await axios.post(
          "http://localhost:8002/api/ai/service",
          {
            cookingMethod: this.selectedMethod,
            cookingCategory: this.selectedCategory,
            ingredients: this.enteredIngredients,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        this.recipes = [response.data];
        this.searched = true;
      } catch (error) {
        console.error("검색 실패:", error);
        this.recipes = [];
        this.searched = true;
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
}
.card {
  border-radius: 10px;
}
.form-select,
.form-control {
  border-radius: 8px;
  border: 1px solid #ced4da;
}
.form-select:focus,
.form-control:focus {
  border-color: #28a745;
  box-shadow: 0 0 5px rgba(40, 167, 69, 0.5);
}
.btn-success {
  background-color: #28a745;
  color: white;
}
.btn-success:hover {
  background-color: #218838;
}
.spinner-border.text-success {
  color: #28a745 !important;
}
</style>
