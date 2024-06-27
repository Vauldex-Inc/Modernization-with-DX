<template>
  <profile-tpl />
</template>

<style lang="scss" scoped>
</style>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import ProfileTpl from '~/components/templates/signup/profile/index.vue'

@Component({
  middleware: 'unauthenticated',
  layout: 'auth-level',
  components: {
    ProfileTpl
  }
})
export default class SignupProfile extends Vue {}
</script>
