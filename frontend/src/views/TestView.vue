<script setup>
import { useDark, useToggle } from "@vueuse/core"
import { ref } from 'vue'

const theme = ref(false)

const isDark = useDark({
  // 存储到localStorage/sessionStorage的key 根据自己的需求更改
  storageKey: "useDarkKEY",
  // 暗黑class名字
  valueDark: "dark",
  // 高亮class名字
  valueLight: "light",
})

const toggle = useToggle(isDark)

</script>

<template>
  <div class="demo">
    <div>这是一个深色模式切换demo</div>
    <el-button inline-prompt v-model="theme" @click="toggle()"></el-button>
  </div>
</template>

<style scoped>

</style>
