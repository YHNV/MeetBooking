<script setup>
import LoginView from '@/views/LoginView.vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import TestView from '@/views/TestView.vue'
import DarkAndLight from '@/views/DarkAndLight.vue'
import HomeView from "@/views/HomeView.vue";
</script>

<template>
  <el-config-provider :locale="zhCn">
    <RouterView />
    <!--<HomeView />-->
    <!-- <TestView /> -->
  </el-config-provider>
</template>

<style scoped></style>
