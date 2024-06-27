<template>
  <div id="wrapper">
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'

@Component
export default class AuthLevel extends Vue {}
</script>
