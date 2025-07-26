<script setup>
import LoginView from '@/views/LoginView.vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <el-config-provider :locale="zhCn">
    <LoginView />
  </el-config-provider>
</template>

<style scoped></style>
